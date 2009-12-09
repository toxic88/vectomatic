/**********************************************
 * Copyright (C) 2009 Lukas Laag
 * This file is part of Vectomatic.
 * 
 * Vectomatic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vectomatic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Vectomatic.  If not, see <http://www.gnu.org/licenses/>
 **********************************************/
package org.vectomatic.common.model.geometry;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to represent a Bezier spline segment in Path
 */
public class BezierSegment extends Segment implements IsSerializable {
	public BezierSegment() {
		// For GWT serialization
	}

	public BezierSegment(Point[] pts) {
		super(pts);
		assert(pts.length == 4);
	}
	
	public BezierSegment(BezierSegment segment) {
		super(segment);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BezierSegment) {
			BezierSegment segment = (BezierSegment)obj;
			for (int i = 0; i < _pts.length; i++) {
				if (!_pts[i].equals(segment._pts[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for (int i = 0; i < _pts.length; i++) {
			code += _pts[i].hashCode();
		}
		return code;
	}

	public Point getStartControlPoint() {
		return _pts[1];
	}
	
	public Point getEndControlPoint() {
		return _pts[2];
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("BezierSegment{");
		buffer.append(_pts[0]);
		buffer.append(", ");
		buffer.append(_pts[1]);
		buffer.append(", ");
		buffer.append(_pts[2]);
		buffer.append(", ");
		buffer.append(_pts[3]);
		buffer.append("}");
		return buffer.toString();
	}
	
	@Override
	public void nearestPointOnSegment(Point p, Point dest) {
		BezierSolver.nearestPointOnCurve(p, _pts).copyTo(dest);
	}

	@Override
	public Segment clone() {
		return new BezierSegment(this);
	}


	/**
	 * Class to determine the point on a Bezier curve
	 * nearest an arbitrary point in the plane.
	 * This code is a java adaptation of the Graphics Gem II
	 * article and code:
	 * A Bezier Curve-Based Root-Finder
	 * by Philip J. Schneider
	 */
	private static class BezierSolver {
		private static final int MAXDEPTH = 64;	// Maximum depth for recursion
		private static final float EPSILON = (float)Math.pow(2, -MAXDEPTH -1);
		private static final int DEGREE	= 3;	// Cubic Bezier curve
		private static final int W_DEGREE = 5;	// Degree of eqn to find roots of
		private static float z[][] = {			// Precomputed "z" for cubics
			{1.0f, 0.6f, 0.3f, 0.1f},
			{0.4f, 0.6f, 0.6f, 0.4f},
			{0.1f, 0.3f, 0.6f, 1.0f},
		    };

		/**
		 * NearestPointOnCurve :
		 * Compute the parameter value of the point on a Bezier
		 * curve segment closest to some arbtitrary, user-input point.
		 * Return the point on the curve at that parameter value.
		 * @param P The user-supplied point
		 * @param V Control points of cubic Bezier
		 */
		public static Point nearestPointOnCurve(Point P, Point[] V) {
		    // Convert problem to 5th-degree Bezier form
		    Point[] w = convertToBezierForm(P, V);

		    // Find all possible roots of 5th-degree equation/
		    float[] t_candidate = new float[W_DEGREE];	// Possible roots     
		    int n_solutions = findRoots(w, W_DEGREE, t_candidate, 0);

		    // Compare distances of P to all candidates, and to t=0, and t=1
			float dist, new_dist;
			Point v = new Point();

			// Check distance to beginning of curve, where t = 0
			dist = P.subtract(V[0], v).squaredLength();
			float t = 0.0f; // Parameter value of closest pt

			// Find distances for candidate points
			for (int i = 0; i < n_solutions; i++) {
				Point p = bezier(V, DEGREE, t_candidate[i], null, null);
				new_dist = P.subtract(p, v).squaredLength();
				if (new_dist < dist) {
					dist = new_dist;
					t = t_candidate[i];
				}
			}

			// Finally, look at distance to end point, where t = 1.0
			new_dist = P.subtract(V[DEGREE], v).squaredLength();
			if (new_dist < dist) {
				dist = new_dist;
				t = 1.0f;
			}
		    
		    // Return the point on the curve at parameter value t
		    return bezier(V, DEGREE, t, null, null);
		}

		/**
		 * ConvertToBezierForm :
		 * Given a point and a Bezier curve, generate a 5th-degree
		 * Bezier-format equation whose solution finds the point on the
		 * curve nearest the user-defined point.
		 * @param P The point to find t for
		 * @param V The control points
		 */
		private static Point[] convertToBezierForm(Point P, Point[] V) {
			Point[] c = new1DPointArray(DEGREE+1);		// V(i)'s - P
			Point[] d = new1DPointArray(DEGREE);		// V(i+1) - V(i)
		    Point[] w = new1DPointArray(W_DEGREE+1);	// Ctl pts of 5th-degree curve

		    float 	cdTable[][] = new float[3][];		// Dot product of c, d
		    for (int i = 0; i < cdTable.length; i++) {
		    	cdTable[i] = new float[4];
		    }

		    // Determine the c's -- these are vectors created by subtracting
		    // point P from each of the control points
		    for (int i = 0; i <= DEGREE; i++) {
		    	V[i].subtract(P, c[i]);
		    }
		    // Determine the d's -- these are vectors created by subtracting
		    // each control point from the next
		    for (int i = 0; i <= DEGREE - 1; i++) { 
		    	V[i+1].subtract(V[i], d[i]).multiply(3f);
		    }

		    // Create the c,d table -- this is a table of dot products of the
		    // c's and d's
		    for (int row = 0; row <= DEGREE - 1; row++) {
				for (int column = 0; column <= DEGREE; column++) {
			    	cdTable[row][column] = d[row].dotProduct(c[column]);
				}
		    }

		    // Now, apply the z's to the dot products, on the skew diagonal
		    // Also, set up the x-values, making these "points"
		    for (int i = 0; i <= W_DEGREE; i++) {
				w[i].y = 0.0f;
				w[i].x = (float)(i) / W_DEGREE;
		    }

		    int n = DEGREE;
		    int m = DEGREE-1;
		    for (int k = 0; k <= n + m; k++) {
				int lb = Math.max(0, k - m);
				int ub = Math.min(k, n);
				for (int i = lb; i <= ub; i++) {
			    	int j = k - i;
			    	w[i+j].y += cdTable[j][i] * z[j][i];
				}
		    }
		    return w;
		}


		/**
		 * FindRoots :
		 * Given a 5th-degree equation in Bernstein-Bezier form, find
		 * all of the roots in the interval [0, 1].  Return the number
		 * of roots found.
		 * @param w The control points
		 * @param degree The degree of the polynomial
		 * @param t RETURN candidate t-values
		 * @param depth The depth of the recursion
		 */
		private static int findRoots(Point[] w, int degree, float[] t, int depth) {  
		    Point[] left = new1DPointArray(W_DEGREE+1);	 // New left and right
		    Point[] right = new1DPointArray(W_DEGREE+1); // control polygons
		    int left_count;		// Solution count from
			int right_count;	// children
		    float left_t[] = new float[W_DEGREE+1];	 //Solutions from kids
		    float right_t[] = new float[W_DEGREE+1];

		    switch (crossingCount(w, degree)) {
				case 0: { // No solutions here
					return 0;
				}
				case 1: { // Unique solution
					// Stop recursion when the tree is deep enough
					// if deep enough, return 1 solution at midpoint
					if (depth >= MAXDEPTH) {
						t[0] = (w[0].x + w[W_DEGREE].x) / 2.0f;
						return 1;
					}
					if (controlPolygonFlatEnough(w, degree)) {
						t[0] = computeXIntercept(w, degree);
						return 1;
					}
					break;
				}
			}

		    // Otherwise, solve recursively after
		    // subdividing control polygon
		    bezier(w, degree, 0.5f, left, right);
		    left_count  = findRoots(left,  degree, left_t, depth+1);
		    right_count = findRoots(right, degree, right_t, depth+1);


		    // Gather solutions together
		    for (int i = 0; i < left_count; i++) {
		        t[i] = left_t[i];
		    }
		    for (int i = 0; i < right_count; i++) {
		 		t[i+left_count] = right_t[i];
		    }

		    // Send back total number of solutions
		    return (left_count+right_count);
		}


		/*
		 * CrossingCount :
		 * Count the number of times a Bezier control polygon 
		 * crosses the 0-axis. This number is >= the number of roots.
		 * @param V Control pts of Bezier curve
		 * @param degree Degreee of Bezier curve
		 */
		private static int crossingCount(Point[] V, int degree) {
		    int 	n_crossings = 0;	/*  Number of zero-crossings	*/
		    float	sign, old_sign;		/*  Sign of coefficients	*/

		    sign = old_sign = Math.signum(V[0].y);
		    for (int i = 1; i <= degree; i++) {
				sign =Math.signum(V[i].y);
				if (sign != old_sign) {
					n_crossings++;
				}
				old_sign = sign;
		    }
		    return n_crossings;
		}



		/**
		 * ControlPolygonFlatEnough :
		 * Check if the control polygon of a Bezier curve is flat enough
		 * for recursive subdivision to bottom out.
		 * @param V Control points
		 * @param degree Degree of polynomial
		 */
		private static boolean controlPolygonFlatEnough(Point[] V, int degree) {
		    float 	distance[] = new float[degree + 1];		/* Distances from pts to line	*/

			// Derive the implicit equation for line connecting first' / and last
			// control points
			float a = V[0].y - V[degree].y;
			float b = V[degree].x - V[0].x;
			float c = V[0].x * V[degree].y - V[degree].x * V[0].y;

		    // Find the  perpendicular distance
		    // from each interior control point to
		    // line connecting V[0] and V[degree]
			float abSquared = (a * a) + (b * b);

			for (int i = 1; i < degree; i++) {
				// Compute distance from each of the points to that line
				distance[i] = a * V[i].x + b * V[i].y + c;
				if (distance[i] > 0.0) {
					distance[i] = (distance[i] * distance[i]) / abSquared;
				}
				if (distance[i] < 0.0) {
					distance[i] = -((distance[i] * distance[i]) / abSquared);
				}
			}

		    // Find the largest distance
			float max_distance_above = 0.0f;
			float max_distance_below = 0.0f;
		    for (int i = 1; i < degree; i++) {
				if (distance[i] < 0.0) {
			    	max_distance_below = Math.min(max_distance_below, distance[i]);
				}
				if (distance[i] > 0.0) {
			    	max_distance_above = Math.max(max_distance_above, distance[i]);
				}
		    }

			// Implicit equation for zero line
			float a1 = 0.0f;
			float b1 = 1.0f;
			float c1 = 0.0f;

			// Implicit equation for "above" line
			float a2 = a;
			float b2 = b;
			float c2 = c + max_distance_above;

			float det = a1 * b2 - a2 * b1;
			float dInv = 1.0f/det;
			
			float intercept_1 = (b1 * c2 - b2 * c1) * dInv;

			// Implicit equation for "below" line
			a2 = a;
			b2 = b;
			c2 = c + max_distance_below;
			
			det = a1 * b2 - a2 * b1;
			dInv = 1.0f/det;
			
			float intercept_2 = (b1 * c2 - b2 * c1) * dInv;

		    // Compute intercepts of bounding box
		    float left_intercept = Math.min(intercept_1, intercept_2);
		    float right_intercept = Math.max(intercept_1, intercept_2);

		    // Compute precision of root
		    float error = 0.5f * (right_intercept-left_intercept);    
		    return error < EPSILON;
		}



		/**
		 *  ComputeXIntercept :
		 *	Compute intersection of chord from first control point to last
		 *  	with 0-axis.
		 *  @param V Control points
		 *  @param degree Degree of curve
		 * 
		 */
		/* NOTE: "T" and "Y" do not have to be computed, and there are many useless
		 * operations in the following (e.g. "0.0 - 0.0").
		 */
		private static float computeXIntercept(Point[] V, int degree) {
		    float XLK = 1.0f - 0.0f;
		    float YLK = 0.0f - 0.0f;
		    float XNM = V[degree].x - V[0].x;
		    float YNM = V[degree].y - V[0].y;
		    float XMK = V[0].x - 0.0f;
		    float YMK = V[0].y - 0.0f;
		    float det = XNM*YLK - YNM*XLK;
		    float detInv = 1.0f/det;
		    float S = (XNM*YMK - YNM*XMK) * detInv;
		    float X = 0.0f + XLK * S;
		    return X;
		}


		/**
		 * Bezier : 
		 * Evaluate a Bezier curve at a particular parameter value
		 * Fill in control points for resulting sub-curves if "Left" and
		 * "Right" are non-null.
		 * @param V Control pts
		 * @param degree Degree of bezier curve
		 * @param t Parameter value
		 * @param Left RETURN left half ctl pts
		 * @param Right RETURN right half ctl pts
		 */
		private static Point bezier(Point[] V, int degree, float t, Point[] Left, Point[] Right) {
		    Point[][] Vtemp = new2DPointArray(W_DEGREE+1, W_DEGREE+1);

		    // Copy control points
		    for (int j = 0; j <= degree; j++) {
				 V[j].copyTo(Vtemp[0][j]);
		    }

		    // Triangle computation
		    for (int i = 1; i <= degree; i++) {	
				for (int j =0 ; j <= degree - i; j++) {
			    	Vtemp[i][j].x =
			      		(1.0f - t) * Vtemp[i-1][j].x + t * Vtemp[i-1][j+1].x;
			    	Vtemp[i][j].y =
			      		(1.0f - t) * Vtemp[i-1][j].y + t * Vtemp[i-1][j+1].y;
				}
		    }
		    
		    if (Left != null) {
				for (int j = 0; j <= degree; j++) {
			    	Vtemp[j][0].copyTo(Left[j]);
				}
		    }
		    if (Right != null) {
				for (int j = 0; j <= degree; j++) {
			    	Vtemp[degree-j][j].copyTo(Right[j]);
				}
		    }

		    return (Vtemp[degree][0]);
		}
		
		private static Point[] new1DPointArray(int d1) {
			Point[] array = new Point[d1];
			for (int i = 0; i < d1; i++) {
				array[i] = new Point();
			}
			return array;
		}
		private static Point[][] new2DPointArray(int d1, int d2) {
			Point[][] array = new Point[d1][];
			for (int i = 0; i < d1; i++) {
				array[i] = new Point[d2];
				for (int j = 0; j < d2; j++) {
					array[i][j] = new Point();
				}
			}
			return array;
		}
	}

}
