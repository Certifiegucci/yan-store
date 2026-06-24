import java.util.ArrayList;
import java.util.List;

// Exception personnalisée
class SoldeInsuffisantException extends Exception {
    public SoldeInsuffisantException(String message) {
        super(message);
    }
}

// Classe abstraite
abstract class Compte {
    private String numero;
    protected double solde;

    public Compte(String numero, double soldeInitial) {
        this.numero = numero;
        this.solde = soldeInitial;
    }

    public String getNumero() {
        return numero;
    }

    public double getSolde() {
        return solde;
    }

    public void deposer(double montant) {
        if (montant > 0) {
            solde += montant;
        }
    }

    public void retirer(double montant) throws SoldeInsuffisantException {
        if (montant > solde) {
            throw new SoldeInsuffisantException(
                "Fonds insuffisants. Solde actuel : " + solde
            );
        }
        solde -= montant;
    }

    public abstract void appliquerInterets();
}

// Compte épargne
class CompteEpargne extends Compte {
    private double tauxInteret;

    public CompteEpargne(String numero, double solde, double tauxInteret) {
        super(numero, solde);
        this.tauxInteret = tauxInteret;
    }

    @Override
    public void appliquerInterets() {
        solde += solde * tauxInteret / 100;
    }
}

// Compte courant
class CompteCourant extends Compte {
    private double decouvertAutorise;

    public CompteCourant(String numero, double solde, double decouvertAutorise) {
        super(numero, solde);
        this.decouvertAutorise = decouvertAutorise;
    }

    @Override
    public void retirer(double montant) throws SoldeInsuffisantException {
        if (solde - montant < -decouvertAutorise) {
            throw new SoldeInsuffisantException(
                "Découvert autorisé dépassé."
            );
        }
        solde -= montant;
    }

    @Override
    public void appliquerInterets() {
        // Pas d'intérêts
    }
}

// Banque
class Banque {
    private List<Compte> comptes = new ArrayList<>();

    public void ajouterCompte(Compte compte) {
        comptes.add(compte);
    }

    public void afficherComptes() {
        System.out.println("\n===== LISTE DES COMPTES =====");
        for (Compte c : comptes) {
            System.out.println(
                "Compte : " + c.getNumero() +
                " | Solde : " + c.getSolde()
            );
        }
    }

    public double totalAvoirs() {
        double total = 0;
        for (Compte c : comptes) {
            total += c.getSolde();
        }
        return total;
    }
}

// Programme principal
public class Main {
    public static void main(String[] args) {

        Banque banque = new Banque();

        CompteEpargne epargne =
            new CompteEpargne("EP001", 100000, 5);

        CompteCourant courant =
            new CompteCourant("CC001", 50000, 20000);

        banque.ajouterCompte(epargne);
        banque.ajouterCompte(courant);

        try {
            epargne.deposer(25000);
            epargne.retirer(15000);

            courant.retirer(60000);

            epargne.appliquerInterets();

        } catch (SoldeInsuffisantException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        banque.afficherComptes();

        System.out.println(
            "\nTotal des avoirs de la banque : "
            + banque.totalAvoirs()
        );
    }
}