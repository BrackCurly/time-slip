(ns time-slip.text-test
  (:require [clojure.test :refer :all]
            [time-slip.text :refer :all]))

(deftest get-words-test
    (testing "split string into words stop-words and seperators"
      (is (= (get-words "Taucher bergen Stimmenrekorder der AirAsia-Maschine")
             '(["Taucher" :noun] ["bergen" :stop] ["Stimmenrekorder" :noun]
               ["der" :stop] ["AirAsia" :noun] ["-" :punctuation] ["Maschine" :noun])))))


(deftest noun-seqs-test
  (let [headline (get-words " \"Zentraler Kampfbegriff\" - \"Lügenpresse\" ist Unwort des Jahres")
        punctuation (get-words "Nächster \"Charlie Hebdo\" wieder mit Mohammed-Karikaturen")]
    (testing
        (is (= (noun-seqs headline)
               (list '(["Zentraler" :noun] ["Kampfbegriff" :noun] ["Lügenpresse" :noun])
                     '(["Unwort" :noun])
                     '(["Jahres" :noun])))))))

(deftest noun-sub-seqs-test
  (testing
      (let [words (get-words "Nächster \"Charlie Hebdo\" wieder mit Mohammed-Karikaturen")]
        (is (= (noun-sub-seqs words)
               (list "Nächster" "Charlie" "Hebdo"
                     "Nächster Charlie"
                     "Charlie Hebdo"
                     "Nächster Charlie Hebdo"
                     "Mohammed" "Karikaturen"
                     "Mohammed - Karikaturen"))))))
