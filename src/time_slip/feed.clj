(ns time-slip.feed
  (:require [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]
            [clojure.string :as string]
            [clj-time.format :as format]
            [clj-time.core :as time]
            [clj-time.coerce :as coerce]))

(defn- parse-str [data]
  (xml/parse (java.io.ByteArrayInputStream. (.getBytes data))))

(defn- strip-tags [s]
  (string/replace s #"(<.*?>)|((&lt|&LT|&#x0003C;|&#60;);.*?(&gt;|&GT;|&#x0003E;|&#62;))" ""))

(def formatter (format/formatters :rfc822))

(defn parse-to-timestamp [s]
     (coerce/to-long (format/parse formatter (string/replace s #"GMT" "+0100"))))

(defn- item-field [item field]
  (zip-xml/xml1-> item field zip-xml/text))

(defn- get-item [item]
  {:title (item-field item :title)
   :timestamp (parse-to-timestamp (item-field item :pubDate))})

(defn parse [data]
  (let [root (-> data parse-str zip/xml-zip)
        items (zip-xml/xml-> root :channel :item)]
    (map get-item items)))
