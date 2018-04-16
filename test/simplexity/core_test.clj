(ns simplexity.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as test]
            [simplexity.core :refer :all]))

(defmacro passes [sym]
  `(is (:check-passed (->> ~sym test/check test/summarize-results))))

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
  (passes `simplexity.core/dim))

(deftest has-size
  (is (- 1 (size (simplex #{0}))))
  (is (- 3 (size (simplex [1 3 6]))))
  (passes `simplexity.core/size))

#_(deftest has-simplicial-facets
    (let [tetrahedron (simplex [0 3 7 12])]
      (is (every? #(s/valid? :simplexity.core/simplex %) (faces tetrahedron)))))
