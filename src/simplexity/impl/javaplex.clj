(ns simplexity.impl.javaplex
  (:require [clojure.math.combinatorics :as combo]
            [simplexity.complex :refer [dim]]
            [cognitect.transcriptor :refer [check!]]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as test])
  (:import [edu.stanford.math.plex4.streams.impl ExplicitSimplexStream]
           [edu.stanford.math.plex4.api Plex4]))

(defn e [] (ExplicitSimplexStream.))

(defn size [s] (.getSize s))

(defn add-vertex
  ([simplex vertex & vs]
   (let [vertices (cons vertex vs)]
     (doall (map #(.addVertex simplex %) vertices))
     simplex)))

(defn add-element [simplex element]
  (.addElement simplex (int-array element))
  simplex)

(s/def ::plex #(= edu.stanford.math.plex4.streams.impl.ExplicitSimplexStream
                  (type %)))

(s/fdef clutter
        :args (s/cat :simplex :simplexity.complex/simplex)
        :ret ::plex
        :fn #(= (count (-> % :args :simplex))
                (.getSize (:ret %))))

(defn clutter [vertices]
  (let [simplex (e)]
    (doall (map #(.addVertex simplex %) vertices))
    simplex))

(test/check `clutter)

(defn- build-simplex [vertices]
  (doto (clutter vertices)  ;; The vertices
    (add-element vertices)  ;; The facet
    (.ensureAllFaces)))     ;; The elements of the facet. Javaplex uses the other definition of faces

(defn- build-strict-complex [facets]
  (let [vertices (set (flatten facets))
        simplex (clutter vertices)]
    (doall (map #(add-element simplex %) facets))
    (.ensureAllFaces simplex)
    (.finalizeStream simplex)
    simplex))

(defmulti build-complex #(first (s/conform :simplexity.complex/complex %)))

(defmethod build-complex :vertex-set
  [vertices]
  (build-simplex vertices))

(defmethod build-complex :facet-set
  [facets]
  (build-strict-complex facets))


;; Homology
;; ---------------------
(defn simplicial-algorithm [d]
  (Plex4/getDefaultSimplicialAlgorithm d))

(defn barcodes [complex]
  (let [plex (build-complex complex)]
    (.computeIntervals (simplicial-algorithm (+ 1 (dim complex))) plex)))

(defn betti-numbers-map
  ([complex]
   (betti-numbers-map complex 0.0))
  ([complex filtration]
   (into {} (.getBettiNumbersMap (barcodes complex) filtration))))

(defn betti [d kom]
  (if-let [value (get (betti-numbers-map kom) d)]
    value
    0))

(defn connected-components [c]
  (betti 0 c))

(defn connected? [s]
  (= (connected-components s) 1))



;; Building a simplicial complex in Javaplex
;; ---------------------------------------------
;; To build a simplicial complex in Javaplex we simply build a stream in which all filtration values are zero. First we create an empty explicit simplex stream. Many command lines in this tutorial will end with a semicolon to supress unwanted output.

;; >> stream = api.Plex4.createExplicitSimplexStream();
;; Next we add simplicies using the methods addVertex and addElement. The first creates a vertex with a specified index, and the second creates a k-simplex (for k >0) with the specified array of vertices. Since we don't specify any filtration values, by default all added simplices will have filtration value zero.

;; >> stream.addVertex(0);
;; >> stream.addVertex(1);
;; >> stream.addVertex(2);
;; >> stream.addElement([0, 1]);
;; >> stream.addElement([0, 2]);
;; >> stream.addElement([1, 2]);
;; >> stream.finalizeStream();
;; After we are done building the complex, calling the method finalizeStream is necessary before working with this complex!

;; We print the total number of simplices in the complex.

;; >> num_simplices = stream.getSize()
;; num_simplices = 6
;; We create an object that will compute the homology of our complex. The first input parameter 3 indicates that homology will be computed in dimensions 0, 1, and 2 — that is, in all dimensions strictly less than 3. The second input 2 means that we will compute homology with Z/2Z coefficients, and this input can be any prime number.

;; >> persistence = api.Plex4.getModularSimplicialAlgorithm(3, 2);
;; We compute and print the intervals.

;; >> intervals = persistence.computeIntervals(stream) 
;; intervals = 

;; Dimension: 1 
;; [0.0, infinity) 
;; Dimension: 0 
;; [0.0, infinity)
;; This gives us the expected Betti numbers Betti0=1 and Betti1=1.

;; The persistence algorithm computing intervals can also find a representative cycle for each interval. However, there is no guarantee that the produced representative will be geometrically nice.

;; >> intervals = persistence.computeAnnotatedIntervals(stream)
;; intervals 

;; Dimension: 1
;; [0.0, infinity): [1,2] + [0,2] + [0,1]
;; Dimension: 0
;; [0.0, infinity): [0]
;; A representative cycle generating the single 0-dimensional homology class is [0], and a representative cycle generating the single 1-dimensional homology class is [1,2] + [0,2] + [0,1].










;; 9-sphere example. Let's build a 9-sphere, which is homeomorphic to the boundary of a 10-simplex. First we add a single 10-simplex to an empty explicit simplex stream. The result is not a simplicial complex because it does not contain the faces of the 10-simplex. We add all faces using the method ensureAllFaces. Then, we remove the 10-simplex using the method removeElementIfPresent. What remains is the boundary of a 10-simplex, that is, a 9-sphere.

;; >> dimension = 9; 
;; >> stream = api.Plex4.createExplicitSimplexStream(); 
;; >> stream.addElement(0:(dimension + 1)); 
;; >> stream.ensureAllFaces(); 
;; >> stream.removeElementIfPresent(0:(dimension + 1)); 
;; >> stream.finalizeStream(); 
;; In the above, the finalizeStream method is used to ensure that the stream has been fully constructed and is ready for consumption by a persistence algorithm. It should be called every time after you build an explicit simplex stream.

;; We print the total number of simplices in the complex.

;; >> num_simplices = stream.getSize()
;; num_simplices = 2046
;; We get the persistence algorithm

;; persistence = api.Plex4.getModularSimplicialAlgorithm(dimension + 1, 2);
;; and compute and print the intervals.

;; >> intervals = persistence.computeIntervals(stream) 
;; intervals = 

;; Dimension: 9
;; [0.0, infinity)
;; Dimension: 0
;; [0.0, infinity)
;; This gives us the expected Betti numbers Betti0=1 and Betti9=1.




;; Representative cycles
;; ------------------------------

;; Quote,
;; The persistence algorithm computing intervals can also find a representative cycle for each interval. However, there is no guarantee that the produced representative will be geometrically nice.

;; >> intervals = persistence.computeAnnotatedIntervals(stream)
;; intervals

;; Dimension: 1
;; [0.0, infinity): [1,2] + [0,2] + [0,1]
;; Dimension: 0
;; [0.0, infinity): [0]
;; A representative cycle generating the single 0-dimensional homology class is [0], and a representative cycle generating the single 1-dimensional homology class is [1,2] + [0,2] + [0,1].

;; This isn't it
#_(defn annotated-intervals [kom]
    (let [plex (build-complex kom)]
      (.computeAnnotatedIntervals (simplicial-algorithm (dim kom)) plex)))

;; Try computing a representative cycle for each barcode.

;; >> intervals = persistence.computeAnnotatedIntervals(stream)
;; We don't display the output from this command in the tutorial, because the representative 9-cycle is very long and contains all eleven 9-simplices.

