(ns time-slip.text-test
  (:require [clojure.test :refer :all]
            [time-slip.text :refer :all]))

(def test-text "Es sollte eine Premiere werden: Die Trägerrakete des Raumfahrtunternehmens SpaceX sollte nach ihrem Flug nicht wie üblich verglühen, sondern wieder landen. Die Trägerrakete konnte noch eine Raumkapsel zur Internationalen Weltraumstation ISS schicken, stürzte jedoch danach ab.")

(deftest get-nouns-test
  (testing "nouns"
    (is (= (get-nouns test-text)
           ["Premiere" "Trägerrakete" "Raumfahrtunternehmens" "SpaceX"
            "Flug" "Trägerrakete" "Raumkapsel" "Weltraumstation" "ISS"]))))
