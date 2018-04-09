package plugin;

import java.util.ArrayList;

public class ComposanteConnexe {

	int numeroCC;
	int couleur;
	long nbPixels;

	public ComposanteConnexe(int numeroCC, int couleur) {
		this.numeroCC = numeroCC;
		this.couleur = couleur;
		nbPixels = 1;
	}
	

	public int getNumeroCC() {
		return numeroCC;
	}
	public int getCouleur() {
		return couleur;
	}

	public long getNbPixel() {
		return nbPixels;
	}

	public void addPixel() {
		nbPixels++;
	}

	@Override
	public String toString() {
		return "ComposanteConnexe [numeroCC=" + numeroCC + ", couleur=" + couleur + ", nbPixels=" + nbPixels + "]";
	}
	

}
