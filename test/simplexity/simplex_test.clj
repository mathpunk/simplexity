(ns simplexity.simplex-test
  (:require [clojure.spec.alpha :as s]
            [checkers.core :refer [passing]]
            [clojure.spec.test.alpha :as test]
            [clojure.test :refer :all]
            [simplexity.simplex :refer :all]))

(def ^:dynamic *short-num-tests* 3)

(deftest is-testing
  (is true))

(deftest constructed-from-integers
  (is (simplex [0 1 2 3]))
  (is (simplex #{0 1 2}))
  (is (simplex '(1 4 5 6))))

(deftest has-dimension
  (is (= 0 (dim (simplex [0]))))
  (is (= 1 (dim (simplex #{0 1}))))
  (is (= 2 (dim (simplex [0 1 2]))))
  (is (passing `simplexity.simplex/dim)))

(deftest has-size
  (is (- 1 (size (simplex #{0}))))
  (is (- 3 (size (simplex [1 3 6]))))
  (is (passing `simplexity.simplex/size)))

(deftest has-elements
  (let [tetrahedron (simplex [0 3 7 12])]
    (is (some #{[0 3 7 12]} (elements tetrahedron)))
    (is (some #{[0 7]} (elements tetrahedron)))
    (is (some #{[12]} (elements tetrahedron)))
    (is (some #{[]} (elements tetrahedron)))
    (is (passing `simplexity.simplex/elements *short-num-tests*))))

(deftest has-faces
  (let [triangle (simplex [0 1 2])]
    (is (= 3 (count (faces triangle))))
    (is (every? (fn [face] (= 2 (size face))) (faces triangle))))
  (is (passing `simplexity.simplex/faces)))

(deftest has-one-facet
  (let [triangle (simplex [0 1 2])]
    (is (= [0 1 2] (first (facets triangle))))
    (is (= 1 (count (facets triangle))))
    (is (passing `simplexity.simplex/facets))))

;; TODO: k-skeletons
#_(deftest has-skeleta
    ...)

(deftest has-the-ball-homology
  (let [pentachoron (simplex #{0 1 2 3 4})
        h (homology pentachoron)]
    (is (= 1 (h 0)))
    (is (passing `simplexity.simplex/homology))))
