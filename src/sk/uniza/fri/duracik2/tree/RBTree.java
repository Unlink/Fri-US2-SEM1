/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.tree;

import static sk.uniza.fri.duracik2.tree.RBTree.Colour.BLACK;
import static sk.uniza.fri.duracik2.tree.RBTree.Colour.RED;

/**
 *
 * @author Unlink
 * @param <E>
 */
public class RBTree<E extends Comparable<? super E>> extends AbstractSearchTree<E> {

	protected static enum Colour {
		RED, BLACK
	};

	protected class RBNode extends Node {

		public Colour aFarba;

		public RBNode(E aPrvok) {
			super(aPrvok);
			aFarba = BLACK;
		}

		/**
		 * Vráti farbu brata, null ak brata nemám lebo som koreň<br>
		 * Ak niesom koreň ale nemám brata tak vráti Colour.BLACK
		 *
		 * @return
		 */
		public Colour getBrotherColour() {
			if (aOtec == null) {
				return null;
			}
			else {
				/*RBNode node = (RBNode) getBrother();
				 if (node == null) {
				 return BLACK;
				 }
				 else {
				 return node.aFarba;
				 }*/
				return colorOf(getBrother());
			}
		}

		@Override
		protected String nodeToString() {
			return super.nodeToString() + ((aFarba == BLACK) ? "b" : "r");
		}

	}

	public RBTree() {
		super();
	}

	public RBTree(TreeIndexer<E> aIndexer) {
		super(aIndexer);
	}

	/**
	 * Vloží prvok do stromu
	 *
	 * @param paData
	 * @return false, ak sa už v strome nachádza
	 */
	public boolean insert(E paData) {
		RBNode newnode = new RBNode(paData);
		if (!insertNode(newnode)) {
			return false;
		}
		if (newnode == aVrchol) {
			newnode.aFarba = BLACK;
		}
		else {
			newnode.aFarba = RED;
			RBNode uzol = (RBNode) newnode.aOtec;
			while (colorOf(uzol) == RED) {
				//Prípad 2 - ak je brat červený
				if (uzol.getBrotherColour() == RED) {
					uzol.aFarba = BLACK;
					((RBNode) uzol.getBrother()).aFarba = BLACK;
					//Pokial otec nieje koreň, spravíme ho čevený
					if (uzol.aOtec != aVrchol) {
						((RBNode) uzol.aOtec).aFarba = RED;
						newnode = (RBNode) uzol.aOtec;
						uzol = (RBNode) newnode.aOtec;
					}
					else {
						break;
					}
				}
				//Prípad1 - Brat je čierny
				else {
					//Rotacia prava
					if (uzol.amILeft()) {
                        //Podrotacia
						//if (uzol.isRightSon(newnode)) { //Nahradena metoda 
						if (newnode.amIRight()) {
							rotate_left(uzol);
						}
						rotate_right(uzol.aOtec);
					}
					else if (uzol.amIRight()) {
						//if (uzol.isLeftSon(newnode)) {
						if (newnode.amILeft()) {
							rotate_right(uzol);
						}
						rotate_left(uzol.aOtec);
					}
					else {
						throw new RuntimeException("Wut? A čí som?");
					}

					if (uzol.aOtec != aVrchol) {
						newnode = (RBNode) uzol;
						uzol = (RBNode) newnode.aOtec;
					}
					else {
						break;
					}
				}
			}

		}
		return true;
	}

	/**
	 * Vymaže prvok zo stromu
	 *
	 * @param paData
	 * @return false ak sa v strome nenachádza
	 */
	public boolean delete(E paData) {
		RBNode node = (RBNode) searchTree(paData);
		//Ak sme prvok v strome nenašli tak končíme
		if (node == null) {
			return false;
		}
		//Keď sme ho už našli, tak sa hádam vymaže :)
		aSize--;

		//Ak má uzol dvoch potomkov tak spravíme náhradu za predchodcu a budeme mazať jeho
		if (node.aLavy != null && node.aPravy != null) {
			//Nahradíme za inorder predchodcu => najpravejšieho z lavého podstromu
			RBNode nahrada = (RBNode) inOrderPredecessor(node);
			node.aPrvok = nahrada.aPrvok;
			//a nastavíme ako mazaný uzol ten z listu
			node = nahrada;
		}

        //Ak mal uzol dvoch synov tak sme ich nahradili nejakým, ktorý nemá dvoch synov, ale stále to nemusí byt list, lebo môže mať lavého syna
		//Taže po zmazaní treba prerobiť referencieň
		//Ak je len koreň, ktorý ma len pravého syna, tak musíme porovnávať aj s pravým synom náhradu
		RBNode nahrada = (RBNode) ((node.aLavy != null) ? node.aLavy : node.aPravy);
		//Mazaný má ešte syna
		if (nahrada != null) {
			if (nahrada.aLavy != null) {
				throw new RuntimeException("WUT?");
			}
			if (nahrada.aPravy != null) {
				throw new RuntimeException("WUT?");
			}
			if (nahrada.aOtec != null && colorOf(nahrada.aOtec) != BLACK) {
				throw new RuntimeException("WUT?");
			}

			if (nahrada.aOtec != aVrchol && colorOf(nahrada) != RED) {
				throw new RuntimeException("WUT?");
			}

			//Mazaný je v koreni
			if (node.aOtec == null) {
				if (node != aVrchol) {
					throw new RuntimeException("WUT?");
				}
				nahrada.aOtec = null;
				aVrchol = nahrada;
			}
			else if (node.amILeft()) {
				node.aOtec.setLeft(nahrada);
			}
			else if (node.amIRight()) {
				node.aOtec.setRight(nahrada);
			}
			else {
				throw new RuntimeException("Wut?");
			}
			nahrada.aFarba = BLACK;
		}
		//Mazaný už nemá syna => je to list alebo koreň a nemá potomkov => vyprazdnenie stromu
		else if (node.aOtec == null) {
			aVrchol = null;
		}
		//Je list
		else {
			if (colorOf(node) == BLACK) {
				fixTree(node);
			}

			//Zmazeme po fixe
			if (node.amILeft()) {
				node.aOtec.aLavy = null;
			}
			else if (node.amIRight()) {
				node.aOtec.aPravy = null;
			}
			else {
				throw new RuntimeException("Wut?");
			}
		}
		return true;

	}

	/**
	 * Opraví strom po zmazaní uzla (dvojitý čierny)
	 *
	 * @param paNode
	 */
	protected void fixTree(RBNode paNode) {
		while (paNode != aVrchol && paNode.aFarba == BLACK) {
			if (paNode.amILeft()) {
				RBNode brat = (RBNode) paNode.getBrother();
				if (brat == null) {
					throw new RuntimeException("Wut?");
				}

				if (colorOf(brat) == RED) {
					rotate_left(paNode.aOtec);
					brat = (RBNode) paNode.getBrother();
				}

				if (colorOf(brat) != BLACK) {
					throw new RuntimeException("Wut?");
				}

				if (colorOf(brat.aLavy) == BLACK && colorOf(brat.aPravy) == BLACK) {
					brat.aFarba = RED;
					paNode = (RBNode) paNode.aOtec;
				}
				else {
					if (colorOf(brat.aPravy) == BLACK && colorOf(brat.aLavy) == RED) {
						rotate_right(brat);
					}
					((RBNode) brat.aPravy).aFarba = BLACK;
					rotate_left(paNode.aOtec);
					return;
				}
			}
			//Symetricky to isté na pravú stranu
			else if (paNode.amIRight()) {
				RBNode brat = (RBNode) paNode.getBrother();
				if (brat == null) {
					throw new RuntimeException("Wut?");
				}

				if (colorOf(brat) == RED) {
					rotate_right(paNode.aOtec);
					brat = (RBNode) paNode.getBrother();
				}

				if (colorOf(brat) != BLACK) {
					throw new RuntimeException("Wut?");
				}

				if (colorOf(brat.aLavy) == BLACK && colorOf(brat.aPravy) == BLACK) {
					brat.aFarba = RED;
					paNode = (RBNode) paNode.aOtec;
				}
				else {
					if (colorOf(brat.aLavy) == BLACK && colorOf(brat.aPravy) == RED) {
						rotate_left(brat);
					}
					((RBNode) brat.aLavy).aFarba = BLACK;
					rotate_right(paNode.aOtec);
					return;
				}
			}
			else {
				throw new RuntimeException("Wut?");
			}
		}
		paNode.aFarba = BLACK;
	}

	/**
	 * Vráti farbu uzla
	 *
	 * @param node
	 * @return
	 */
	private Colour colorOf(Node node) {
		return (node != null && ((RBNode) node).aFarba == RED) ? RED : BLACK;
	}

	/**
	 * Otestuje strom na RB pravidlá
	 *
	 * @return
	 */
	public boolean testTreeRules() {
		System.out.print("Kontrola stromu - ");
		if (aVrchol == null) {
			System.out.println("OK - Strom je prázdny");
			return true;
		}
		else {
			//Koren je čierny
			if (((RBNode) aVrchol).aFarba != BLACK) {
				System.out.println("ERR - Koren nieje čierrny");
				return false;
			}
			int pocCiernych = 0;
			Node uzol = aVrchol;
			while (uzol != null && uzol.aLavy != null) {
				if (((RBNode) uzol).aFarba == BLACK) {
					pocCiernych++;
				}
				uzol = uzol.aLavy;
			}

			if (testRekurzia((RBNode) aVrchol, 0, pocCiernych)) {
				System.out.println("OK - Strom je RB (Počet čiernych na ceste z listu do koreňa = " + pocCiernych + ")");
				return true;
			}
			else {
				return false;
			}

		}
	}

	private boolean testRekurzia(RBNode uzol, int pocet, int checksum) {
		if (uzol.aOtec != null) {
			if (((RBNode) uzol.aOtec).aFarba == RED && uzol.aFarba == RED) {
				System.out.println("ERR - Porušenie pravidla dvoch červených");
				return false;
			}
			if (uzol.aFarba == BLACK) {
				pocet++;
			}
			if (uzol.aLavy != null) {
				return testRekurzia((RBNode) uzol.aLavy, pocet, checksum);
			}
			else {
				if (pocet != checksum) {
					System.out.println("ERR - Porušenie pravidlo počtu čierných ku každému listu");
					return false;
				}
			}

			if (uzol.aPravy != null) {
				return testRekurzia((RBNode) uzol.aPravy, pocet, checksum);
			}
			else {
				if (pocet != checksum) {
					System.out.println("ERR - Porušenie pravidlo počtu čierných ku každému listu");
					return false;
				}
			}

			return true;
		}
		else {
			if (uzol.aFarba != BLACK) {
				System.out.println("ERR - Koreň nieje black");
				return false;
			}
			return true;
		}
	}

}
