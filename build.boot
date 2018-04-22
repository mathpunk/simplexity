(def project 'simplexity)
(def version "0.4.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "RELEASE"]
                            [appliedtopology/javaplex "4.3.1"]
                            [org.clojure/math.combinatorics "0.1.4"]
                            [adzerk/boot-test "RELEASE" :scope "test"]
                            [orchestra "2017.11.12-1"]
                            [org.clojure/core.match "0.3.0-alpha5"]
                            [org.clojure/test.check "0.9.1-SNAPSHOT" :scope "test"]
                            [com.cognitect/transcriptor "0.1.5" :scope "test"]])

(task-options!
 pom {:project     project
      :version     version
      :description "Functions for topological data analysis"
      :url         "http://github.com/mathpunk/simplexity"
      :scm         {:url "https://github.com/mathpunk/simplexity"}
      :license     {"None I guess?"
                    "https://media.giphy.com/media/gr1ayQmhUafWE/giphy.gif"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(require '[adzerk.boot-test :refer [test]])
