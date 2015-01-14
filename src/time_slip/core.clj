(ns time-slip.core
  (:require [clj-http.client :as client]
            [clj-time.core :as t]
            [clj-time.coerce :as coerce]
            [time-slip.text :as txt]
            [time-slip.feed :as f]))

(def feeds ["http://www.tagesschau.de/xml/rss2"
            "http://www.faz.net/rss/aktuell/"
            "http://newsfeed.zeit.de/news/index"])

(def start-date (coerce/to-long (t/today-at 0 0)))

(defn parse-res [res]
  (-> res :body f/parse))

(defn nouns-freq [s]
  (->> s
       txt/get-words
       txt/noun-sub-seqs
       frequencies))

(defn freq-super [[s n] nouns-freq]
  (->> nouns-freq
       (take-while #(>= (/ (second %) 2) n))
       (some #(re-matches (re-pattern s) (first %)))))

(defn main []
  (let [nouns-freq (->> feeds
                        (map client/get)
                        (filter #(= (:status %) 200))
                        (mapcat parse-res)
                        (map :title)
                        (map nouns-freq)
                        (apply merge-with +)
                        (sort-by second)
                        reverse)
        most-freq (first nouns-freq)
        most-freq-super (freq-super most-freq nouns-freq)]


  (first (if (nil? most-freq-super)
    most-freq
    most-freq-super))))
