import java.util.ArrayList;

import javax.swing.JOptionPane;

import processing.core.PApplet;

public class Tree extends PApplet {
	private GenTree parent;
	private GenTree[][] trees;

	double newGene = 2; // TODO add as settings
	double removeGene = 2;
	double changeGene = 6;

	public static int W = 10;
	public static int H = W;

	private int TAB = 0;

	private static final float SCALE = 5;

	public void settings() {
		size(800, 800);
		fullScreen();
	}

	public void setup() {
		trees = new GenTree[W][H];
		parent = new GenTree(10);
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				trees[i][j] = new GenTree(10);
			}
		}

	}

	/*
	 * 
	 * Main loop
	 */
	public void draw() {
		background(255);
		if (TAB == 0) {
			for (int i = 0; i < W; i++) {
				for (int j = 0; j < H; j++) {
					drawTree(trees[i][j], this.width / (W * 2) + this.width / W * i,
							this.height / (H * 2) + this.height / H * j);

				}
			}
		}
		if (TAB == 1) {
			drawTreeScaled(parent, this.width / 2, this.height / 2, SCALE);
		}
	}

	public static void main(String args[]) {
		PApplet.main("Tree", args);
	}

	public void keyPressed() {
		if (key == 'p') {
			if (TAB == 1) {
				TAB = 0;
			} else {
				TAB = 1;
			}
			delay(100);
		}
		if (key == 's') {
			String s = JOptionPane.showInputDialog(null,
					"What is the file path including the file type at the end (png or jpg)");
			save(s);
		}
		if (key == 'h') {
			JOptionPane.showConfirmDialog(null, "p - show parent \ns - to save \n t - settings");
		}
	}

	public void mousePressed() {
		int x = (int) (mouseX / (this.width / W));
		int y = (int) (mouseY / (this.height / H));

		parent = trees[x][y];
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				trees[i][j] = parent.getChild();
			}
		}
	}

	/*
	 * 
	 * Helper Methods
	 */
	public void drawTreeScaled(GenTree g, float x, float y, float scale) {
		tree(g, 0, x, y, (float) (-Math.PI / 2), scale);
	}

	public void drawTree(GenTree g, float x, float y) {
		tree(g, 0, x, y, (float) (-Math.PI / 2), 1);
	}
	
	/**
	 * Draws a tree given parameters (recursive)
	 * @param g - the tree to be drawn
	 * @param n - stage of recursion (kind of)
	 * @param x - start x location
	 * @param y - start y location
	 * @param a - 
	 * @param scale
	 */

	private void tree(GenTree g, int n, float x, float y, float a, float scale) {
		if (g.geneSize() < n * 3 + 3) {
			return;
		}
		int splits = Math.abs((int) (g.getGene(n * 3 + 2)));
		float length = g.getGene(n * 3) * 5 * scale;
		float angle = (float) (Math.PI / 6 * g.getGene(n * 3 + 1));
		drawLine(x, y, length, a);
		if (splits == 0) {
			return;
		} else if (splits == 1) {
			tree(g, n + 1, getX(x, length, a), getY(y, length, a), a + angle, scale);
		} else {
			for (int i = 0; i < splits; i++) {
				tree(g, n + 1, getX(x, length, a), getY(y, length, a), a - (angle / 2) * (splits - 1) + i * angle,
						scale);
			}
		}
	}
	
	/*
	 * 
	 * Helper Methods
	 */

	public void drawLine(float x, float y, float l, float a) {
		this.line(x, y, (float) (x + l * Math.cos(a)), (float) (y + l * Math.sin(a)));
	}

	public float getX(float x, float l, float a) {
		return (float) (x + l * Math.cos(a));
	}

	public float getY(float y, float l, float a) {
		return (float) (y + l * Math.sin(a));
	}

	
	/**
	 * 
	 * @author sasha
	 * Class that keeps the genetic data of each "tree"
	 */
	private class GenTree {
		
		// every 3 entries correspond to a single level of the tree
		private ArrayList<Float> genes; // length,angle,splits ...

		
		//list of all previous trees that led up to the current
		// TODO implement loading and path finding for "history" array
		private ArrayList<GenTree> history;

		private GenTree() {
			genes = new ArrayList<Float>();
			history = new ArrayList<GenTree>();
		}

		/**
		 * Creates a tree with k genes
		 * @param k number of genes
		 */
		public GenTree(int k) {
			genes = new ArrayList<Float>();
			history = new ArrayList<GenTree>();
			for (int i = 0; i < k; i++) {
				this.addGene(rand(5));
			}
		}

		/**
		 * Creates a child with a random mutation
		 * @return a randomly generated genTree from parent Tree
		 */
		public GenTree getChild() {
			GenTree g = this.getClone();
			int r = (int) (Math.random() * (g.genes.size()));
			double t = newGene + removeGene + changeGene;
			double a = Math.random() * t;
			if (a < newGene) {
				for (int i = 0; i < 2; i++) {
					g.addGene(rand(1));
				}
			} else if (a < removeGene + newGene) {
				g.genes.remove(r);
			} else {
				g.genes.set(r, g.genes.get(r) + rand(5));
			}
			return g;

		}

		/**
		 * Creates an identical tree
		 * @return
		 */
		
		private GenTree getClone() {
			GenTree g = new GenTree();
			for (int i = 0; i < genes.size(); i++) {
				g.addGene(genes.get(i));
			}
			return g;
		}
		
		
		
		/*
		 * 
		 * Helper Methods
		 */

		private float rand(float a) {
			return (float) (Math.random() * a * 2 - a);
		}

		private void addGene(float k) {
			genes.add(k);
		}

		public int geneSize() {
			return genes.size();
		}

		public float getGene(int k) {
			return genes.get(k);
		}

	}
}
