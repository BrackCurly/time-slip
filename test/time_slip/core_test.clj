(ns time-slip.core-test
  (:require [clojure.test :refer :all]
            [time-slip.core :refer :all]))


(deftest replace-special-chars-test
  (testing
      (is (= (replace-special-chars "Der Flügel des Bärenkönigs auf der Straße")
             "Der Fluegel des Baerenkoenigs auf der Strasse")))
  (testing
      (is (= (replace-special-chars "Übel, das Öl der Äste")
          "Uebel, das Oel der Aeste"))))
