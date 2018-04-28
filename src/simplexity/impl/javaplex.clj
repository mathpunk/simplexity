(ns simplexity.impl.javaplex
  (:require [clojure.math.combinatorics :as combo]
            [simplexity.complex]
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

(defn- build-simplex [vertices]
  (doto (clutter vertices)  ;; The vertices
    (add-element vertices)  ;; The facet
    (.ensureAllFaces)))     ;; The elements of the facet. Javaplex uses the other definition of faces

(defn- build-strict-complex [facets]
  (let [vertices (set (flatten facets))
        simplex (clutter vertices)]
    (doall (map #(add-element simplex %) facets))
    (.ensureAllFaces simplex)
    simplex))

(defmulti build-complex #(first (s/conform :simplexity.complex/complex %)))

(defmethod build-complex :vertex-set
  [vertices]
  (build-simplex vertices))

(defmethod build-complex :facet-set
  [facets]
  (build-strict-complex facets))
