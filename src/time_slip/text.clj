(ns time-slip.text
  (:require [clojure.string :as str]))

(def punctuation-tokens #{"." "-" "„" "“" "’" "'" "\"" "/"})

;; match ignored characters or hyphen between spaces
(def ignore-regex  #"[:,;!?+]|((?<=\s)-(?=\s))")

;; match before or after punctuation char or multiple whitespaces
(def split-regex  #"(?<=[.\-„“\"’'/])|(?=[.\-„“\"’'/])|\s{1,}")

(defn- punctuation-token? [token]
  (contains? punctuation-tokens token))

(defn- stop-token? [token]
  (= token (str/lower-case token)))

(defn- token-type [token]
  (cond
   (punctuation-token? token) :punctuation
   (stop-token? token) :stop
   :else :noun))

(defn- get-tokens [s]
  (-> s
      (str/replace ignore-regex "")
      (str/split split-regex)))

(defn- get-word [token]
  [token (token-type token)])

(defn get-words [s]
  "splits string into seq of word tuples [token, type]"
  (->> (get-tokens s)
       (map get-word)))

(defn- take-last-while [pred col]
  (->> col
      reverse
      (take-while pred)
      reverse))

(defn- split-by [pred col]
  (let [head (take-while (comp not pred) col)
        tail (drop (count head) col)]
    [head tail]))

(defn- noun? [[_ type]]
  (= type :noun))

(defn- stop? [[_ type]]
  (= type :stop))

(defn noun-seqs
  "returns all sequences of nouns containing the token.
   Seqence may contain punctuation tokens"
  ([token words] (noun-seqs token [] (split-by #(= (first %) token) words)))
  ([token seqs [head [word & tail]]]
     (if (= token (first word))
       (let [seq (concat (take-last-while #(not (stop? %)) head)
                         [word]
                         (take-while #(not (stop? %)) tail))
             seqs (concat seqs [seq])]
         (if (not (next tail))
           seqs
           (recur token seqs (split-by #(= (first %) token) tail))))
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
