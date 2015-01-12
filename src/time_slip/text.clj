(ns time-slip.text
  (:require [opennlp.nlp :as nlp]
            [clojure.string :as str]))

(def tokenize (nlp/make-tokenizer "models/de-token.bin"))
(def pos-tag (nlp/make-pos-tagger "models/de-pos-maxent.bin"))

(defn get-words [s]
  "splits string into seq of word tuples [word, type]"
  (->> s
       tokenize
       pos-tag))

(defn- noun? [[_ type]]
  (contains? #{"FM" "NN" "NE"} type))

(defn- noun=word? [noun [str _]]
  (= noun str))

(defn- take-last-while [pred col]
  (->> col
      reverse
      (take-while pred)
      reverse))

(defn- split-by [pred col]
  (let [head (take-while (comp not pred) col)
        tail (drop (count head) col)]
    [head tail]))

(defn noun-seqs
  "returns all subsequences of nouns containing the noun"
  ([noun words] (noun-seqs noun [] (split-by #(noun=word? noun %) words)))
  ([noun seqs [head [word & tail]]]
     (if (noun=word? noun word)
       (let [seq (concat (take-last-while noun? head)
                         [word]
                         (take-while noun? tail))
             seqs (concat seqs [seq])]
         (if (not (next tail))
           seqs
           (recur  noun seqs (split-by #(noun=word? noun %) tail))))
       seqs)))

(defn- freq-map [xs]
  (-> xs
      frequencies
      (into (sorted-map))))

(defn most-significant-noun [words]
  (let [[[noun _] noun-n] (->> words (filter noun?) freq-map first)
        [word-seq seq-n] (->> words (noun-seqs noun) freq-map first)]
    (if (> seq-n (- noun-n seq-n))
      (str/join " " (map first word-seq))
      noun)))
