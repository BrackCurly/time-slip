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

(defn noun-seq-freq [[s n] nouns-freq]
  (->> (drop 1 nouns-freq)
       (take-while (fn [[_ n]] (> n 1)))
       (filter (fn[[s-super _]] (.contains s-super s)))))

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
        noun (first nouns-freq)
        noun-seq (first (noun-seq-freq noun nouns-freq))]
    (if (nil? noun)
      ""
      (if (nil? noun-seq)
      (first noun)
      (if (> (/ (second noun) 2) (second noun-seq))
        (first noun)
        (first noun-seq))))))
