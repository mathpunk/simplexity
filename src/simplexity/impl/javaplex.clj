(ns simplexity.impl.javaplex
  (:require [clojure.math.combinatorics :as combo]
            [simplexity.simplex]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as test])
  (:import [edu.stanford.math.plex4.streams.impl ExplicitSimplexStream]
           [edu.stanford.math.plex4.api Plex4]))


;; The New API
;; --------------------
;; Goal: Support passing homology tests in complex_test.clj


;; Let's start by learning to construct javaplex simplexes.

(defn e [] (ExplicitSimplexStream.))

(.getSize (e))
(check! #{0})

(.getSize (doto (e)
            (.addVertex 0)))
(check! #{1})

(.getSize (doto (e)
            (.addVertex 0)
            (.addVertex 1)))
(check! #{2})

(.getSize
 (doto (e)
   (.addVertex 0)
   (.addVertex 1)
   (.addElement (int-array [0,1]))))
(check! #{3}) ;; surprise!

(defn add-vertex [simplex vertex]
  (.addVertex simplex vertex))

(defn add-element [simplex element]
  (.addElement simplex (int-array element)))

(.getSize
 (doto (e)
   (add-vertex 0)
   (add-vertex 1)
   (add-element [0 1])))


;; Isolating mutation. ???

(def c
  (doto (e)
    (add-vertex 0)
    (add-vertex 1)
    (add-vertex 2)
    (add-element [0 1 2])))
;; .getSize is 4

(.ensureAllFaces c)

(.getSize c)
;; .getSize is 7

(def t
  (doto (e)
    (add-vertex 0)
    (add-vertex 1)
    (add-vertex 2)
    (add-vertex 3)
    (add-element [0 1 2])
    (add-element [1 2 3])
    (add-element [0 1 3])
    (add-element [0 3 2])))

(.getSize t)
;; 8???
(.ensureAllFaces t)
(.getSize t)
;; 14
;; I guess that's right. Yes. That's right. It isn't a valid complex until that ensureAllFaces has happened.

(betti-numbers-map t)
#_{0 1, 1 3, 2 4}
;; what WHAT that doesn't look right at all.

(s/def ::plex #(= edu.stanford.math.plex4.streams.impl.ExplicitSimplexStream
                  (type %)))


(s/fdef clutter
        :args (s/cat :simplex :simplexity.simplex/simplex)
        :ret ::plex
        :fn #(= (count (-> % :args :simplex))
                (.getSize (:ret %))))

(defn clutter [vertices]
  (let [simplex (e)]
    (doall (map #(.addVertex simplex %) vertices))
    simplex))

(test/check `clutter)








(.getSize (clutter [0 1 2 3 4 5]))


;; Works but user-hostile
;; --------------------
(defn empty-simplex [] (ExplicitSimplexStream.))


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
