(ns sentinel.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response response not-found]]
            [ring.middleware.reload :refer [wrap-reload]])
  (:import (java.net InetAddress Socket InetSocketAddress)))


(defn reachable?
  [addr open-port]
  (try
    (let [socket (Socket.)]
      (.connect socket (InetSocketAddress. ^Integer addr ^String open-port) 1000))
    true
    (catch Exception e
      false)))


(defroutes routes
           (GET "/" [] (resource-response "index.html" {:root "public"}))
           (GET "/ping/:url" [url] (response [{:url    url
                                               :status (if (reachable? url 80) :up :down)}]))
           (resources "/")
           (not-found "Page not found"))

(def dev-handler (-> #'routes wrap-reload))

(def handler routes)
