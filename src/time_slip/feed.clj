(ns time-slip.feed
  (:require [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]))

(defn- parse-str [data]
  (xml/parse (java.io.ByteArrayInputStream. (.getBytes data))))

(defn parse
  [data]
  (let [root (-> data
                 parse-str
                 zip/xml-zip)
        items (zip-xml/xml-> root
                             :channel
                             :item)]
    items))
