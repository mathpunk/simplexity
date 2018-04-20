(ns simplexity.impl.javaplex-test
  (:require  [clojure.test :refer [deftest testing is]]
             [checkers.core :refer [passing]]
             [cognitect.transcriptor :as xr]
             [clojure.spec.test.alpha :as test]))

(deftest run-repl-interaction
  (is (nil? (xr/run "./test/simplexity/javaplex_building.repl"))))

(deftest generative
  (is (passing `simplexity.impl.javaplex/clutter)))

