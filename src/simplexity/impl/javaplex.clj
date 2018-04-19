(ns simplexity.impl.javaplex
  (:require [clojure.math.combinatorics :as combo])
  (:import [edu.stanford.math.plex4.streams.impl ExplicitSimplexStream]
           [edu.stanford.math.plex4.api Plex4]))

(defn empty-simplex [] (ExplicitSimplexStream.))

(defn clutter [vertices]
  (let [e (empty-simplex)]
    (doall (map #(.addVertex e %) vertices))
    e))

(.getSize (clutter [0 1 2 3]))
(check! #{4})

#_(defn integral-simplex [vertices]
    (let [s (clutter vertices)]
      (.ensureAllFaces s)
      (.finalizeStream s)
      s))

(defn integral-simplex [vertices]
  (let [s (clutter vertices)
        edges (->> vertices
                   combo/subsets
                   (remove #(<= (count %) 1))
                   (map int-array))]
    (doall (map #(.addElement s %) edges))
    s))

(.getSize (integral-simplex [0 1 2]))
(check! #{7})

;; checks out ;;;

(def triangle
  (doto (ExplicitSimplexStream.)
    (.addVertex 0)
    (.addVertex 1)
    (.addVertex 2)
    (.addElement (int-array [0 1]))
    (.addElement (int-array [1 2]))
    (.addElement (int-array [2 0]))
    (.finalizeStream)))

(def solid-triangle
  (doto (ExplicitSimplexStream.)
    (.addVertex 0)
    (.addVertex 1)
    (.addVertex 2)
    (.addElement (int-array [0 1]))
    (.addElement (int-array [1 2]))
    (.addElement (int-array [2 0]))
    (.addElement (int-array [0 1 2]))
    (.finalizeStream)))

(def edge-glued-triangles
  (doto (ExplicitSimplexStream.)
    (.addVertex 0)
    (.addVertex 1)
    (.addVertex 2)
    (.addVertex 3)
    (.addElement (int-array [0 1]))
    (.addElement (int-array [1 2]))
    (.addElement (int-array [2 0]))
    (.addElement (int-array [1 3]))
    (.addElement (int-array [2 3]))
    (.finalizeStream)))




(def ^:dynamic *max-dimension* 5)

(def ^:dynamic *simplicial-algorithm*
  (Plex4/getDefaultSimplicialAlgorithm *max-dimension*))

(defn barcodes [complex]
  ;; TODO: ensure 'finalized'?
  (.computeIntervals *simplicial-algorithm* complex))

(defn betti-numbers-map
  ([complex]
   (betti-numbers-map complex 0.0))
  ([complex filtration]
   (into {} (.getBettiNumbersMap (barcodes complex) filtration))))

(defn connected-components [s]
  (get (betti-numbers-map s) 0))

(defn connected? [s]
  (= (connected-components s) 1))

((betti-numbers-map edge-glued-triangles) 0)
(check! #{1})
((betti-numbers-map edge-glued-triangles) 1)
(check! #{2})



;; Okay. That all checks out, it's just that the api sucks.

;; BETTER API MAYBE

;; ;; The Empty Simplex: Properties unknown
;; (defn e [] (ExplicitSimplexStream.))

;; (defn algorithm-by-dimension [d]
;;   (Plex4/getDefaultSimplicialAlgorithm d))

;; (.getSize (e))
;; (check! #{0})

