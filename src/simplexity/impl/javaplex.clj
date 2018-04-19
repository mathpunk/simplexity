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
(check! #{3})

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

(defn build-simplex [vertices]
  (let [simplex (clutter vertices)]
    (.ensureAllFaces simplex)
    ;; BUT NO ELEMENTS! so it's three
    simplex))

(let [simplex (clutter [0 1 2])]
  (.ensureAllFaces simplex)
  (.getSize simplex))
(check! #{3})

(let [simplex (clutter [0 1 2])]
  (add-element simplex [0 1 2])
  (.ensureAllFaces simplex)
  (.getSize simplex))
(check! #{7})

(let [simplex (clutter [0 1 2])]
  (add-element simplex [0 1])
  (add-element simplex [1 2])
  (add-element simplex [2 0])
  (.ensureAllFaces simplex)
  (.getSize simplex))
(check! #{6})

;; See how this works? You put in some elements, and .ensureAllFaces makes sure that it's a valid complex from that.

#_(.getSize (build-complex [[0 1] [1 2] [2 0]]))
#_(check! #{6})

#_(.getSize (build-complex [[0 1  2]]))
#_(check! #{7})

#_(.getSize (build-complex [0 1  2]))
;; Should it be 7 or should it throw?






;; Works but user-hostile
;; --------------------

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
