
/**
 * org.hermit.android.instrument: graphical instruments for Android.
 * <br>Copyright 2009 Ian Cameron Smith
 * 
 * <p>These classes provide input and display functions for creating on-screen
 * instruments of various kinds in Android apps.
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation (see COPYING).
 * 
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */


package com.yifeijiang.android.audio;



import org.hermit.dsp.FFTTransformer;
import org.hermit.dsp.SignalPower;
import org.hermit.dsp.Window;


/**
 * An {@link Instrument} which analyses an audio stream in various ways.
 * 
 * <p>To use this class, your application must have permission RECORD_AUDIO.
 */
public class AudioAnalyser
{

	Listener listener;
	/**
     * Listener for audio reads.
     */
    public static abstract class Listener {
        public static final int ERR_OK = 0;
        public static final int ERR_INIT_FAILED = 1;
        public static final int ERR_READ_FAILED = 2;

        public abstract void onReadComplete(short[] buffer);
        public abstract void onAnalyzeComplete(float[] buffer);
        
        /**
         * An error has occurred.  The reader has been terminated.
         * @param   error       ERR_XXX code describing the error.
         */
        public abstract void onReadError(int error);
    }
    
	
	
    // ******************************************************************** //
    // Constructor.
    // ******************************************************************** //

	/**
	 * Create a WindMeter instance.
	 * 
     * @param   parent          Parent surface.
	 */
    public AudioAnalyser(Listener lis) {
    	listener = lis;
        audioReader = new AudioReader();
        
        spectrumAnalyser = new FFTTransformer(inputBlockSize, windowFunction);
        
        // Allocate the spectrum data.
        spectrumData = new float[inputBlockSize / 2];
        spectrumHist = new float[inputBlockSize / 2][historyLen];
        spectrumIndex = 0;

        biasRange = new float[2];
    }


    // ******************************************************************** //
    // Configuration.
    // ******************************************************************** //

    /**
     * Set the sample rate for this instrument.
     * 
     * @param   rate        The desired rate, in samples/sec.
     */
    public void setSampleRate(int rate) {
        sampleRate = rate;
    }
    

    /**
     * Set the input block size for this instrument.
     * 
     * @param   size        The desired block size, in samples.  Typical
     *                      values would be 256, 512, or 1024.  Larger block
     *                      sizes will mean more work to analyse the spectrum.
     */
    public void setBlockSize(int size) {
        inputBlockSize = size;

        spectrumAnalyser = new FFTTransformer(inputBlockSize, windowFunction);

        // Allocate the spectrum data.
        spectrumData = new float[inputBlockSize / 2];
        spectrumHist = new float[inputBlockSize / 2][historyLen];
    }
    

    /**
     * Set the spectrum analyser windowing function for this instrument.
     * 
     * @param   func        The desired windowing function.
     *                      Window.Function.BLACKMAN_HARRIS is a good option.
     *                      Window.Function.RECTANGULAR turns off windowing.
     */
    public void setWindowFunc(Window.Function func) {
        windowFunction = func;
        spectrumAnalyser.setWindowFunc(func);
    }
    

    /**
     * Set the decimation rate for this instrument.
     * 
     * @param   rate        The desired decimation.  Only 1 in rate blocks
     *                      will actually be processed.
     */
    public void setDecimation(int rate) {
        sampleDecimate = rate;
    }
    
    
    /**
     * Set the histogram averaging window for this instrument.
     * 
     * @param   len         The averaging interval.  1 means no averaging.
     */
    public void setAverageLen(int len) {
        historyLen = len;
        
        // Set up the history buffer.
        spectrumHist = new float[inputBlockSize / 2][historyLen];
        spectrumIndex = 0;
    }
    

    // ******************************************************************** //
    // Run Control.
    // ******************************************************************** //

    public void appStart() {
    }


    /**
     * We are starting the main run; start measurements.
     */
    public void measureStart() {
        audioProcessed = audioSequence = 0;
        readError = AudioReader.Listener.ERR_OK;
        
        audioReader.startReader(sampleRate, inputBlockSize * sampleDecimate, new AudioReader.Listener() {
            @Override
            public final void onReadComplete(short[] buffer) {
                receiveAudio(buffer);
            }
            @Override
            public void onReadError(int error) {
                handleError(error);
            }
        });
    }


    /**
     * We are stopping / pausing the run; stop measurements.
     */
    public void measureStop() {
        audioReader.stopReader();
    }
    

    /**
     * The application is closing down.  Clean up any resources.
     */
    public void appStop() {
    }
    
    
    // ******************************************************************** //
    // Audio Processing.
    // ******************************************************************** //

    /**
     * Handle audio input.  This is called on the thread of the audio
     * reader.
     * 
     * @param   buffer      Audio data that was just read.
     */
    private final void receiveAudio(short[] buffer) {
        // Lock to protect updates to these local variables.  See run().
        synchronized (this) {
            audioData = buffer;
            ++audioSequence;
            listener.onReadComplete(buffer);
            
        }
    }
    
    
    /**
     * An error has occurred.  The reader has been terminated.
     * 
     * @param   error       ERR_XXX code describing the error.
     */
    private void handleError(int error) {
        synchronized (this) {
            readError = error;
        }
    }


    // ******************************************************************** //
    // Main Loop.
    // ******************************************************************** //

    /**
     * Update the state of the instrument for the current frame.
     * This method must be invoked from the doUpdate() method of the
     * application's {@link SurfaceRunner}.
     * 
     * <p>Since this is called frequently, we first check whether new
     * audio data has actually arrived.
     * 
     * @param   now         Nominal time of the current frame in ms.
     */
    public final void doUpdate(long now) {
        short[] buffer = null;
        synchronized (this) {
            if (audioData != null && audioSequence > audioProcessed) {
                //parentSurface.statsCount(1, (int) (audioSequence - audioProcessed - 1));
                audioProcessed = audioSequence;
                buffer = audioData;
            }
        }

        // If we got data, process it without the lock.
        if (buffer != null)
            processAudio(buffer);
        
        if (readError != AudioReader.Listener.ERR_OK){
            processError(readError);
            listener.onAnalyzeComplete(spectrumData);
        }
        
    }


    /**
     * Handle audio input.  This is called on the thread of the
     * parent surface.
     * 
     * @param   buffer      Audio data that was just read.
     */
    private final void processAudio(short[] buffer) {
        // Process the buffer.  While reading it, it needs to be locked.
        synchronized (buffer) {
            // Calculate the power now, while we have the input
            // buffer; this is pretty cheap.
            final int len = buffer.length;

            // If we have a spectrum or sonagram analyser, set up the FFT input data.
            spectrumAnalyser.setInput(buffer, len - inputBlockSize, inputBlockSize);

            // Tell the reader we're done with the buffer.
            buffer.notify();
        }

        // If we have a spectrum or sonagram analyser, perform the FFT.
        
        // Do the (expensive) transformation.
        // The transformer has its own state, no need to lock here.
        long specStart = System.currentTimeMillis();
        spectrumAnalyser.transform();
        long specEnd = System.currentTimeMillis();
        //parentSurface.statsTime(0, (specEnd - specStart) * 1000);

        // Get the FFT output.
        if (historyLen <= 1)
            spectrumAnalyser.getResults(spectrumData);
        else
            spectrumIndex = spectrumAnalyser.getResults(spectrumData,
                                                        spectrumHist,
                                                        spectrumIndex);
        
        listener.onAnalyzeComplete(spectrumData);
        // If we have a spectrum gauge, update data and draw.
        //if (spectrumGauge != null)
        //    spectrumGauge.update(spectrumData);
        	
        // If we have a sonagram gauge, update data and draw.
        //if (sonagramGauge != null)
        //    sonagramGauge.update(spectrumData);

    }
    

    /**
     * Handle an audio input error.
     * 
     * @param   error       ERR_XXX code describing the error.
     */
    private final void processError(int error) {
        // Pass the error to all the gauges we have.
    }
    

    // ******************************************************************** //
    // Save and Restore.
    // ******************************************************************** //

    /**
     * Save the state of the system in the provided Bundle.
     * 
     * @param   icicle      The Bundle in which we should save our state.
     */


    /**
     * Restore the system state from the given Bundle.
     * 
     * @param   icicle      The Bundle containing the saved state.
     */
    

    // ******************************************************************** //
    // Class Data.
    // ******************************************************************** //

    // Debugging tag.
	@SuppressWarnings("unused")
	private static final String TAG = "instrument";

	
	// ******************************************************************** //
	// Private Data.
	// ******************************************************************** //

    // Our parent surface.

    // The desired sampling rate for this analyser, in samples/sec.
    private int sampleRate = 8000;

    // Audio input block size, in samples.
    private int inputBlockSize = 256;
    
    // The selected windowing function.
    private Window.Function windowFunction = Window.Function.BLACKMAN_HARRIS;

    // The desired decimation rate for this analyser.  Only 1 in
    // sampleDecimate blocks will actually be processed.
    private int sampleDecimate = 1;
   
    // The desired histogram averaging window.  1 means no averaging.
    private int historyLen = 4;

    // Our audio input device.
    private final AudioReader audioReader;

    // Fourier Transform calculator we use for calculating the spectrum
    // and sonagram.
    private FFTTransformer spectrumAnalyser;
    
    // The gauges associated with this instrument.  Any may be null if not
    // in use.
    
    // Buffered audio data, and sequence number of the latest block.
    private short[] audioData;
    private long audioSequence = 0;
    
    // If we got a read error, the error code.
    private int readError = AudioReader.Listener.ERR_OK;
    
    // Sequence number of the last block we processed.
    private long audioProcessed = 0;

    // Analysed audio spectrum data; history data for each frequency
    // in the spectrum; index into the history data; and buffer for
    // peak frequencies.
    private float[] spectrumData;
    private float[][] spectrumHist;
    private int spectrumIndex;
   
    // Current signal power level, in dB relative to max. input power.
    private double currentPower = 0f;

    // Temp. buffer for calculated bias and range.
    private float[] biasRange = null;

}

