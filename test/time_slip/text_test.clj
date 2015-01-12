(ns time-slip.text-test
  (:require [clojure.test :refer :all]
            [time-slip.text :refer :all]))

(deftest get-words-test
    (testing "nouns"
      (is (= (get-words "Ruft die Feuerwehr!")
             '(["Ruft" "NN"] ["die" "ART"] ["Feuerwehr" "NN"] ["!" "$."])))))

(deftest noun-seqs-test
  (let [words (get-words "Peter Müller mag Frau Sahra Müller und Angie.")]
    (testing "noun at start of sequence"
      (is (= (noun-seqs "Peter" words)
             (list (list ["Peter" "NE"] ["Müller" "NE"])))))
    (testing "noun in the middle of sequence"
      (is (= (noun-seqs "Sahra" words)
             (list (list ["Frau" "NN"] ["Sahra" "NE"] ["Müller" "NE"])))))
    (testing "noun at the end of sequence"
      (is (= (noun-seqs "Müller" words)
             (list
              (list ["Peter" "NE"] ["Müller" "NE"])
              (list ["Frau" "NN"] ["Sahra" "NE"] ["Müller" "NE"])))))))

(deftest most-significant-noun-test
  (let [words-single (get-words "Das Schicksal ist im Spiel. Unser Schicksal tragen wir.")
        words-group (get-words "Angela Merkel ist Bundeskanzlerin. Angela Merkel ist eine Frau.")]
        (testing "most significant noun is single word"
          (is (= (most-significant-noun words-single) "Schicksal")))
        (testing "most significant noun is group of words"
          (is (= (most-significant-noun words-group) "Angela Merkel")))))
