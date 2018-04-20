(ns simplexity.impl.javaplex
  (:require [clojure.math.combinatorics :as combo]
            [simplexity.simplex]
            [simplexity.complex]
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

(require '[clojure.pprint :refer (pprint)])

(map pprint (s/exercise :simplexity.simplex/simplex))

(test/check `clutter)

(defn build-simplex [vertices]
  (doto (clutter vertices)  ;; The vertices
    (add-element vertices)  ;; The facet
    (.ensureAllFaces)))     ;; The elements of the facet. Javaplex uses the other definition of faces

(.getSize (build-simplex [0 1 2]))
(check! #{7})

(defn build-strict-complex [facets]
  (let [vertices (set (flatten facets))
        simplex (clutter vertices)]
    (doall (map #(add-element simplex %) facets))
    (.ensureAllFaces simplex)
    simplex))

(.getSize (build-strict-complex [[0 1] [1 2] [2 0]]))
(check! #{6})

(.getSize (build-strict-complex [[0 1 2]]))
(check! #{7})

(s/conform :simplexity.complex/complex [0 1 2])
(s/conform :simplexity.complex/complex [[0 1 2]])

(defmulti build-complex #(first (s/conform :simplexity.complex/complex %)))

(defmethod build-complex :simplex
  [vertices]
  (build-simplex vertices))

(defmethod build-complex :complex
  [facets]
  (build-strict-complex facets))

(.getSize (build-complex [0 1 2]))
(check! #{7})

(.getSize (build-complex [[0 1 2]]))
(check! #{7})
