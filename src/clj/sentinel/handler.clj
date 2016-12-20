(ns sentinel.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response response not-found]]
            [ring.middleware.reload :refer [wrap-reload]])
  (:import (java.net InetAddress)))

(defn ping
  [host]
  (.isReachable (InetAddress/getByName host) 1000))


(defroutes routes
           (GET "/" [] (resource-response "index.html" {:root "public"}))
           (GET "/ping/:url" [url] (response [{:url    url
                                               :status "up"}]))
           (resources "/")
           (not-found "Page not found"))

(def dev-handler (-> #'routes wrap-reload))

(def handler routes)
