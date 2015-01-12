(ns time-slip.text
  (:require [opennlp.nlp :as nlp]))

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
         #_(println seq head tail)
         (if (not (next tail))
           seqs
           (recur  noun seqs (split-by #(noun=word? noun %) tail))))
       seqs)))
