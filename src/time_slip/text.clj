(ns time-slip.text
  (:require [opennlp.nlp :as nlp]))


(def tokenize (nlp/make-tokenizer "models/de-token.bin"))
(def pos-tag (nlp/make-pos-tagger "models/de-pos-maxent.bin"))

(defn- noun? [[_ pos]]
  (contains? #{"FM" "NN" "NE"} pos))

(defn get-nouns [s]
  (->> s
      tokenize
      pos-tag
      (filter noun?)
      (map first)))
