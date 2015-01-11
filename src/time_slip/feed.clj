(ns time-slip.feed
  (:require [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]
            [clojure.string :as string]
            [clj-time.format :as format]
            [clj-time.core :as time]))

(defn- parse-str [data]
  (xml/parse (java.io.ByteArrayInputStream. (.getBytes data))))

(defn- strip-tags [s]
  (string/replace s #"(<.*?>)|((&lt|&LT|&#x0003C;|&#60;);.*?(&gt;|&GT;|&#x0003E;|&#62;))" ""))

(def formatter (format/formatter "EEE, dd MMM yyyy HH:mm:ss Z"))

(defn- parse-date [s]
  (format/parse formatter s))

(defn- item-field [item field]
  (zip-xml/xml1-> item field zip-xml/text))

(defn- get-item [item]
  (let [title (item-field item :title)
        description (item-field item :description)
        date (item-field item :pubDate)]
    {:text (strip-tags (str title " " description))
     :date (parse-date date)}))

(defn parse [data]
  (let [root (-> data parse-str zip/xml-zip)
        items (zip-xml/xml-> root :channel :item)]
    (map get-item items)))
