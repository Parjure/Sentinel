(ns sentinel.connector.consul.consul
  (:require [consul.core :as consul]))

(defn alive?
  [serviceID]
  "Check if a service i alive or not"
  (contains? (set (keys (consul/agent-checks :local)))
             (str "service:" serviceID)))

(defn registered-services
  []
  "Return all registered service in local consul node"
  (map #((val %) :ID) (consul/agent-services :local)))
