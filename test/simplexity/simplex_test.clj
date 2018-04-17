(ns simplexity.simplex-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as test]
            [clojure.test :refer :all]
            [simplexity.simplex :refer :all]))

(defn passing
  ([sym] (:check-passed (->> sym test/check test/summarize-results)))
  ([sym n] (:check-passed
            (test/summarize-results
             (test/check sym
                         {:clojure.spec.test.check/opts {:num-tests n}})))))

(def ^:dynamic *short-num-tests* 3)

(deftest is-testing
  (is true))

(deftest constructed-from-integers
  (is (simplex [0 1 2 3]))
  (is (simplex #{0 1 2}))
  (is (simplex '(1 4 5 6))))

(deftest has-dimension
  (is (= 0 (dim (simplex [0]))))
  (is (= 1 (dim (simplex [0 1]))))
  (is (= 2 (dim (simplex [0 1 2]))))
  (is (passing `simplexity.core/dim)))

(deftest has-size
  (is (- 1 (size (simplex #{0}))))
  (is (- 3 (size (simplex [1 3 6]))))
  (is (passing `simplexity.core/size)))

(deftest has-simplicial-faces
  (let [tetrahedron (simplex [0 3 7 12])]
    (is (every? #(s/valid? :simplexity.core/simplex %) (faces tetrahedron)))
    (is (some #{[0 3 7 12]} (faces tetrahedron)))
    (is (some #{[0 7]} (faces tetrahedron)))
    (is (some #{[12]} (faces tetrahedron)))
    (is (some #{[]} (faces tetrahedron)))
    (is (passing `simplexity.core/faces *short-num-tests*))))

;; TODO: the k-skeleton

(deftest has-one-facet
  (let [triangle (simplex [0 1 2])]
    (is (= [0 1 2] (first (facets triangle))))
    (is (= 1 (count (facets triangle))))
    ))

(deftest has-the-expected-homology
  (let [pentachoron (simplex #{0 1 2 3 4})
        h (homology pentachoron)]
    (= 1 (h 0))
    ))
