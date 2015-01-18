(ns time-slip.text
  (:require [clojure.string :as str]))

(def stop-tokens #{"der" "die" "das" "in" "und" "sind" "ist" "ein" "zu" "von" "haben" "werden" "mit"
                  "an" "auf" "sich" "für" "nicht" "es" "sie" "er" "auch" "als" "bei" "dies" "dass"
                  "können" "aus" "eine" "ich" "nach" "wie" "ihr" "um" "aber" "so" "nur" "noch" "Jahr"
                  "über" "wir" "viel" "man" "oder" "vor" "müssen" "all" "sollen" "kein" "bis" "sagt"
                  "sagen" "wollen" "will"})

(def punctuation-tokens #{"." "-" "’" "'" "\""})

;; match ignored characters or hyphen between spaces
(def ignore-regex  #"[:,;!?+„“\"]|((?<=\s)-(?=\s))")

;; match before or after punctuation char or multiple whitespaces
(def split-regex  #"(?<=[.\-’'/])|(?=[.\-’'/])|\s{1,}")

(defn- punctuation-token? [token]
  (contains? punctuation-tokens token))

(defn- stop-token? [token]
  (let [lower-token (str/lower-case token)]
    (or (= token lower-token)
        (contains? stop-tokens lower-token))))


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

(defn- punctuation? [[_ type]]
  (= type :punctuation))

(defn noun-seqs
  "returns all sequences of nouns.
  Seqences may contain punctuation tokens"
  ([words] (noun-seqs [] (split-by #(not (stop? %)) words)))
  ([seqs [head [word & tail]]]
     (if (and (not (nil? word)) (not (stop? word)))
       (let [seq-head (take-last-while #(not (stop? %)) head)
             seq-tail (take-while #(not (stop? %)) tail)
             seq (concat seq-head [word] seq-tail)
             seqs (concat seqs [seq])
             tail (drop (count seq-tail) tail)]
         (if (not (next tail))
           seqs
           (recur seqs (split-by #(not (stop? %)) tail))))
       seqs)))

(defn- sub-seqs [seq]
  (->> (for [n (range 1 (inc (count seq)))]
         (partition n 1 seq))
       (apply concat)))

(defn- noun-seq? [word-seq]
  (and (-> word-seq first punctuation? not)
       (-> word-seq last punctuation? not)))

(defn- detokenize [words]
  (str/join " " (map first words)))

(defn noun-sub-seqs [words]
  (->> words
      noun-seqs
      (mapcat sub-seqs)
      (filter noun-seq?)
      (map detokenize)))
