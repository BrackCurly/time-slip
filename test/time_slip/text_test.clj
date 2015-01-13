(ns time-slip.text-test
  (:require [clojure.test :refer :all]
            [time-slip.text :refer :all]))

(deftest get-words-test
    (testing "split string into words stop-words and seperators"
      (is (= (get-words "Taucher bergen Stimmenrekorder der AirAsia-Maschine")
             '(["Taucher" :noun] ["bergen" :stop] ["Stimmenrekorder" :noun]
               ["der" :stop] ["AirAsia" :noun] ["-" :punctuation] ["Maschine" :noun])))))


(deftest noun-seqs-test
  (let [words (get-words "Peter Müller mag Frau Sahra Müller und Angie.")]
    (testing "noun at start of sequence"
      (is (= (noun-seqs "Peter" words)
             (list [["Peter" :noun]  ["Müller" :noun]]))))
    (testing "noun in the middle of sequence"
      (is (= (noun-seqs "Sahra" words)
             (list [["Frau" :noun] ["Sahra" :noun] ["Müller" :noun]]))))
    (testing "noun at the end of sequence"
      (is (= (noun-seqs "Müller" words)
             (list
              [["Peter" :noun] ["Müller" :noun]]
              [["Frau" :noun] ["Sahra" :noun] ["Müller" :noun]]))))))

(deftest most-significant-noun-test
  (let [words-single (get-words "Das Schicksal ist im Spiel. Unser Schicksal tragen wir.")
        words-group (get-words "Angela Merkel ist Bundeskanzlerin. Angela Merkel ist eine Frau.")]
        (testing "most significant noun is single word"
          (is (= (most-significant-noun words-single) "Das Schicksal")))
        (testing "most significant noun is group of words"
          (is (= (most-significant-noun words-group) "Angela Merkel")))))
