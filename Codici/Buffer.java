public class Buffer {	
	// variabili d'istanza
	private String buffer;
	
	// costruttore
	public Buffer() {
		buffer = null;
	}
	
	// metodo per verificare se il buffer è vuoto
	public boolean vuoto() {	
		return buffer==null;
	}
	
	// metodo che copia nel buffer la frase passatagli come parametro
	public synchronized void scrivi(String frase) {	
		while(!vuoto()) {	
			try {
				wait();		/*	Diversamente da prima, dato che ci possono essere più produttori
								che nel tempo invocheranno il metodo "scrivi()", dopo il primo produttore
								i successivi dovranno attendere che un consumatore abbia pulito il buffer
								prima di scriverci un nuovo valore.
								Ora dunque anche un produttore (attraverso l'invocazione del metodo "scrivi()")
								dovrà sincronizzarsi/essere svegliato da un consumatore
							*/
			}
			catch(InterruptedException e) { System.out.println(e); }
		}	
		buffer = frase;
		notifyAll();	// ora ho più thread consumatori da svegliare
	}
	
	// metodo che rimane in attesa di leggere il contenuto del buffer non appena questo viene scritto
	public synchronized String leggi() {
		while(vuoto()) {
			try {
				wait();
			}
			catch(InterruptedException e) { System.out.println(e); }
		}
		String frase = buffer;
		buffer = null;
		notifyAll();	/*	Diversamente da prima, ora i consumatori che invocano "leggi()" devono,
							al termine della lettura, svegliare i produttori successivi
							(prima avevo 1 solo produttore per cui, una volta che questo aveva agito a inizio programma,
							il consumatore non aveva altri produttori da svegliare)
						*/
		return frase;
	
	}

}
