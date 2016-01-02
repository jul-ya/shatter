package com.shatter.dt;

import java.util.HashSet;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;

public class Test {

	public static void main(String[] args) {
		
		HashSet<Edge> e = new HashSet<Edge>();
		Triangle t = new Triangle(new Vector2(0.5f,0.5f), new Vector2(1.5f,1.5f), new Vector2(1.0f,1.0f));
		
		e.add(new Edge(t.getA(), t.getB()));
		e.add(new Edge(t.getB(), t.getC()));
		e.add(new Edge(t.getB(), t.getA()));
		e.add(new Edge(t.getC(), t.getB()));
		
		System.out.println(e.size());
		
		Iterator<Edge> i = e.iterator();
		while(i.hasNext()){
			Edge ee = i.next();
			System.out.print(ee.a.toString());
			System.out.println(ee.b.toString());
		}
		
	}

}
