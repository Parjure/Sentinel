(ns sentinel.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response response not-found]]
            [ring.middleware.reload :refer [wrap-reload]]
            [sentinel.connector.consul.consul :as consul-connector])
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
           (GET "/ping/consul/services" [] (response (pr-str (->> (consul-connector/registered-services)
                                                                  (map (fn [serviceID]
                                                                         {:server-name serviceID
                                                                          :status      (if (consul-connector/alive? serviceID)
                                                                                         "up"
                                                                                         "down")}))
                                                                  (vec)))))
           (GET "/ping/consul/services/:serviceID" [serviceID] (response [{:url    (str "consul/services/" serviceID)
                                                                           :status (if (consul-connector/alive? serviceID)
                                                                                     "up"
                                                                                     "down")}]))
           (GET "/ping/:url" [url] (response [{:url    url
                                               :status (if (reachable? url 80) :up :down)}]))
           (resources "/")
           (not-found "Page not found"))

(def dev-handler (-> #'routes wrap-reload))

(def handler routes)
