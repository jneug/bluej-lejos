package de.upb.bluej.lejos;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LeJOSProcess {
	
	private ProcessBuilder builder;
	
	private Process process = null;
	
	private List<InputStreamListener> errorListener = new ArrayList<InputStreamListener>(2);
	private List<InputStreamListener> outListener = new ArrayList<InputStreamListener>(2);
	
	public LeJOSProcess( ProcessBuilder pb ) {
		this.builder = pb;
	}
	
	public LeJOSProcess( Process p ) {
		this.process = p;
		this.start();
	}
	
	public boolean start() {
		try {
			if( this.process == null && this.builder != null )
				this.process = this.builder.start();
			if( this.process == null )
				return false;
			
			StreamObserver outOb = new StreamObserver(process.getInputStream(), outListener);
			StreamObserver errOb = new StreamObserver(process.getErrorStream(), errorListener);
			
			new Thread(outOb).start();
			new Thread(errOb).start();
			
			return true;
		} catch( IOException e ) {
			return false;
		}
	}
	
	public ProcessBuilder getProcessBuilder() {
		return this.builder;
	}
	
	public Process getProcess() {
		return this.process;
	}
	
	public void addErrorListener( InputStreamListener esl ) {
		errorListener.add(esl);
	}
	
	public void addOutputListener( InputStreamListener osl ) {
		outListener.add(osl);
	}
	

	public static interface InputStreamListener {
		public void nextLine( String line );
	}
	
	
	private static final class StreamObserver implements Runnable {
		private Scanner in;
		private List<InputStreamListener> listeners;
		private boolean running = false;
		public StreamObserver( InputStream is, List<InputStreamListener> listeners ) {
			this.in = new Scanner(is);
			this.listeners = listeners;
		}
		@Override
		public void run() {
			this.running = true;
			while( this.running ) {
				try {
					String nl = in.nextLine();
					
					for( InputStreamListener osl: this.listeners ) {
						osl.nextLine(nl);
					}
				} catch( NoSuchElementException e1 ) {
					this.running = false;
				} catch( IllegalStateException e2 ) {
					this.running = false;
				}
			}
		}
	}
	
}
