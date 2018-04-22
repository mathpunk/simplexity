(ns simplexity.interactions-test
  (:require  [cognitect.transcriptor :as xr]
             [clojure.test :refer [deftest testing is]]))

(comment
  (xr/repl-files "./test")
  )

(deftest test-complex
  (is (not (xr/run "./test/simplexity/complex.repl"))))

(deftest test-javaplex-building
  (is (not (xr/run "./test/simplexity/javaplex_building.repl"))))

(deftest test-simplex-invariants
  (is (not (xr/run "./test/simplexity/simplex_invariants.repl"))))
