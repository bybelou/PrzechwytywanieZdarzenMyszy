package paczka;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PrzechwytywanieZdarzenMyszy {
	public static void main(String[] args) {
		Ramka ramka = new Ramka();
		ramka.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}


class Ramka extends JFrame {
	private static final long serialVersionUID = 1L;

	public Ramka() {
		setTitle("Aplikacja rysuj¹ca, kasuj¹ca i przesuwaj¹ca kwadraciki");
		setSize(500,300);
		
		Panel panel = new Panel();
		Container kontener = getContentPane();
		kontener.add(panel);
		//this.pack(); //inicjalizuje cala ramke

		setVisible(true);
	}
}


class Panel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	ArrayList<Rectangle2D> listaKwadratow = new ArrayList<Rectangle2D>();
	//final static int BOK_KWADRATU = 10;
	final static int BOK_KWADRATU = 50;
	
	public Panel() {
		setBackground(SystemColor.window);
		
		SluchaczMyszki sluchaczMyszki = new SluchaczMyszki();
		addMouseListener(sluchaczMyszki);
		SluchaczRuchowMyszki sluchaczRuchowMyszki = new SluchaczRuchowMyszki();
		addMouseMotionListener(sluchaczRuchowMyszki);
		
		setVisible(true);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		rysujKwadraty(g2);
	}
	
	private void dodajKwadrat(Point2D punkt) {
		listaKwadratow.add(new Rectangle2D.Double(punkt.getX()-BOK_KWADRATU/2, punkt.getY()-BOK_KWADRATU/2,
													BOK_KWADRATU, BOK_KWADRATU));
		System.out.println("dodaje kwadrat");
	}
	
	private void usunKwadrat(Rectangle2D kwadrat){
		for (int i=0; i<listaKwadratow.size(); i++) {
			if (listaKwadratow.get(i).equals(kwadrat)){
				listaKwadratow.remove(i);
				System.out.println("usuwam kwadrat");
			}
		}
	}
	
	private Rectangle2D przesunKwadrat(Rectangle2D tymczasowyKwadrat, Point2D nowyPunkt) {
		for (int i=0; i<listaKwadratow.size(); i++) {
			if (listaKwadratow.get(i).equals(tymczasowyKwadrat)){
				listaKwadratow.set(i, new Rectangle2D.Double(nowyPunkt.getX()-BOK_KWADRATU/2, nowyPunkt.getY()-BOK_KWADRATU/2,
										BOK_KWADRATU, BOK_KWADRATU));
				System.out.println("przesuwam kwadrat");
				return listaKwadratow.get(i);
			}
		}
		return null;
	}
	
	private Rectangle2D znajdzKwadrat(Point2D punkt, boolean czyDodajeNowyKwadrat) {
		for (int i=0; i<listaKwadratow.size(); i++) {
			Rectangle2D tymczasowyKwadrat = null;
			if (czyDodajeNowyKwadrat) {
				tymczasowyKwadrat = new Rectangle2D.Double(listaKwadratow.get(i).getCenterX()-BOK_KWADRATU,
																listaKwadratow.get(i).getCenterY()-BOK_KWADRATU,
															BOK_KWADRATU*2,
																BOK_KWADRATU*2);
			} else {
				tymczasowyKwadrat = listaKwadratow.get(i);
			}
			if (tymczasowyKwadrat.contains(punkt)) {
				return tymczasowyKwadrat;
			}
		}
		
		return null;
	}
	
	private void rysujKwadraty(Graphics2D grafika) {
		for (int i=0; i<listaKwadratow.size(); i++) {
			grafika.draw(listaKwadratow.get(i));
		}
	}
	
	
	private class SluchaczMyszki extends MouseAdapter {//klasa implementuje MouseListener
		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println("kliknieto przycisk " + e.getClickCount() + " razy");
			
			Point2D punkt = e.getPoint();
			
			if (e.getClickCount() == 1) {//jesli to pierwsze klikniecie - dodaj kwadrat
				if (znajdzKwadrat(punkt, true) == null) {//ma szukac tak aby nie nachodzily na siebie kwadraciki
					dodajKwadrat(punkt);
					repaint();
				}
			} else if (e.getClickCount() == 2) {//jesli to drugie klikniecie - usun kwadrat
				Rectangle2D tymczasowyKwadrat = znajdzKwadrat(punkt, false);//ma szukac tylko wewnatrz kwadracika, a nie tak zeby nachodzily
				if (tymczasowyKwadrat != null) {
					usunKwadrat(tymczasowyKwadrat);
					repaint();
				}
			}
		}
	}
	
	private class SluchaczRuchowMyszki extends MouseMotionAdapter{//klasa implementuje MouseMotionListener
		boolean przenoszenieKwadratu = false;
		Rectangle2D tymczasowyKwadrat = null;
		
		@Override
		public void mouseDragged(MouseEvent e) {
			//System.out.println("przeciagnieto");
			
			Rectangle2D sprawdzamKwadrat = znajdzKwadrat(e.getPoint(), false);//ma szukac tylko wewnatrz kwadracika, a nie tak zeby nachodzily
			if (sprawdzamKwadrat != null && przenoszenieKwadratu == false) {
				przenoszenieKwadratu = true;
				tymczasowyKwadrat = sprawdzamKwadrat; 
			}
			
			if (przenoszenieKwadratu) {
				Point2D nowyPunkt = e.getPoint();
				tymczasowyKwadrat = przesunKwadrat(tymczasowyKwadrat,nowyPunkt);
				repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			//System.out.println("przesunieto");
			
			przenoszenieKwadratu = false;
			tymczasowyKwadrat = null;
		}		
	}
}