package unblockme_solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UBMSolver {
	
	final static int dim = 6;
	
	public static void main(String[] args) {
		//String world = "X    Y  Y   p     y   x     y X     ";
		//String world = "x  Yy X     p   y y x     yx  x     ";
		//String world = "  Y         p     X                 ";
		try {
			System.out.println("INPUT:\n" + printWorld(args[0]));
			System.out.println("SOLUTION:\n" + printWorlds(solution(args[0])));
		} catch (Exception e) {
			System.err.println(e);
		}
		//String world = "  Y         p        X              ";
		//System.out.println(printWorld(world));
		//System.out.println(printWorlds(getWorlds(world)));
	}
	
	static Map<String, List<String>> states = new HashMap<String, List<String>>();
	static List<String> queue = new ArrayList<String>();
	static List<String> nextQueue = new ArrayList<String>();
	static String cworld = "";
	static List<String> cpath = new ArrayList<String>();
	
	static List<String> solution(String world) {
		states.clear();
		queue.clear();
		nextQueue.clear();
		cworld = world;
		cpath.clear();
		
		states.put(world, new ArrayList<String>(cpath));
		
		bodyloop:
		while ( !isFinal(cworld) ) {
			List<String> temp = new ArrayList<String>(getWorlds(cworld));
			
			for (String nworld : temp) {
				if ( !states.containsKey(nworld) ) {
					List<String> npath = new ArrayList<String>(cpath);
					npath.add(cworld);
					states.put(nworld, npath);
					nextQueue.add(nworld);
					
					if (isFinal(nworld)) {
						cworld = nworld;
						cpath = states.get(cworld);
						break bodyloop;
					}
				}
			}
			
			if (queue.size() == 0) {
				queue.addAll(nextQueue);
				nextQueue.clear();
			}
			cworld = queue.get(0);
			cpath = states.get(cworld);
			queue.remove(0);
		}
		
		List<String> rsolution = new ArrayList<String>(cpath);
		rsolution.add(cworld);
		return rsolution;
	}
	
	static Set<String> getWorlds(String world) {
		Set<String> rworlds = new HashSet<String>();
		int spaces = world.length(); 
		for (int i = 0; i < spaces; i++) {
			if (world.charAt(i) != ' ') {
				rworlds.addAll(getMoves(world, i));
			}
		}
		return rworlds;
	}
	
	static Set<String> getMoves(String world, int idx) {
		Set<String> mworlds = new HashSet<String>();
		char[] arrWorld = world.toCharArray();
		char piece = arrWorld[idx];
		arrWorld[idx] = ' ';
		String mworld = new String(arrWorld);
		
		int cidx;
		
		if (piece == 'y' || piece == 'Y') {
			cidx = idx;
			cidx = up(cidx);
			while(isValid(mworld, piece, cidx)) {
				arrWorld[cidx] = piece;
				mworlds.add(new String(arrWorld));
				arrWorld[cidx] = ' ';
				cidx = up(cidx);
			}
			
			cidx = idx;
			cidx = down(cidx);
			while(isValid(mworld, piece, cidx)) {
				arrWorld[cidx] = piece;
				mworlds.add(new String(arrWorld));
				arrWorld[cidx] = ' ';
				cidx = down(cidx);
			}
		} else {
			cidx = idx;
			cidx = left(cidx);
			while(isValid(mworld, piece, cidx)) {
				arrWorld[cidx] = piece;
				mworlds.add(new String(arrWorld));
				arrWorld[cidx] = ' ';
				cidx = left(cidx);
			}
			
			cidx = idx;
			cidx = right(cidx);
			while(isValid(mworld, piece, cidx)) {
				arrWorld[cidx] = piece;
				mworlds.add(new String(arrWorld));
				arrWorld[cidx] = ' ';
				cidx = right(cidx);
			}
		}
		
		return mworlds;
	}
	
	static boolean isValid(String world, char id, int idx) {
		switch (id) {
		case 'x':
			return isOpen(world, idx) && isOpen(world, right(idx));
		case 'X':
			return isOpen(world, idx) && isOpen(world, right(idx)) && isOpen(world, right(right(idx)));
		case 'p':
			return isOpen(world, idx) && isOpen(world, right(idx));
		case 'y':
			return isOpen(world, idx) && isOpen(world, down(idx));
		case 'Y':
			return isOpen(world, idx) && isOpen(world, down(idx)) && isOpen(world, down(down(idx)));
		default:
			return false;
		}
	}
	
	static boolean isOpen(String world, int idx) {
		if (idx == -1 || world.charAt(idx) != ' ') {
			return false;
		}
		
		int t;
		char c;
		
		t = left(idx);
		if (t != -1) {
			c = world.charAt(t);
			if (c == 'x' || c == 'X' || c == 'p') return false;
		}
		t = left(left(idx));
		if (t != -1) {
			c = world.charAt(t);
			if (c == 'X') return false;
		}
		
		t = up(idx);
		if (t != -1) {
			c = world.charAt(t);
			if (c == 'y' || c == 'Y') return false;
		}
		t = up(up(idx));
		if (t != -1) {
			c = world.charAt(t);
			if (c == 'Y') return false;
		}
		
		return true;
	}
	
	static boolean isFinal(String world) {
		return world.charAt(16) == 'p'; // hardcoded for 6x6
	}
	
	static int left(int idx) {
		int col = idx % dim;
		return (col > 0 ? idx - 1 : -1);
	}
	
	static int right(int idx) {
		int col = idx % dim;
		return (col < dim - 1 ? idx + 1 : -1);
	}
	
	static int up(int idx) {
		int row = idx / dim;
		return (row > 0 ? idx - dim : -1);
	}
	
	static int down(int idx) {
		int row = idx / dim;
		return (row < dim - 1 ? idx + dim : -1);
	}
	
	static String printStates() {
		String print = "";
		for (String world : states.keySet()) {
			for (int i = 0; i < dim * dim; i++) {
				char c = world.charAt(i);
				if (c == ' ' && !isOpen(world, i)) {
					c = 'O';
				}
				print += c;
				if ((i + 1) % dim == 0) {
					print += "\n";
				}
			}
			print += "\n";
		}
		return print;
	}
	
	static String printWorld(String world) {
		String print = "------+\n";
		for (int i = 0; i < dim * dim; i++) {
			char c = world.charAt(i);
			if (c == ' ' && !isOpen(world, i)) {
				c = 'O';
			}
			print += c;
			if ((i + 1) % dim == 0) {
				print += "|\n";
			}
		}
		print += "------+\n";
		return print;
	}
	
	static String printWorlds(Iterable<String> worlds) {
		String print = "======\n";
		for (String world : worlds) {
			print += printWorld(world);
			print += "\n";
		}
		return print;
	}
}
