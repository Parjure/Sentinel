(ns sentinel.views
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [reagent.core :as reagent]))

(defn title []
  [re-com/title
   :label (str "Sentinel")
   :level :level1])

(defn server-box [name url]
  (let [_      (println "asking for " name " & " url)
        status (re-frame/subscribe [:get-server-status [name url]])]
    [re-com/v-box
     :style {:background-color "lightgray"}
     :margin "15px"
     :padding "10px"
     :children [[re-com/title :label name :level :level1]
                [re-com/title :label @status]]]))

(defn add-bar []
  [re-com/h-box
   ;:size "auto"
   :style {}
   :children [
              [re-com/input-text
               :placeholder "server name"
               :model ""
               :on-change #(re-frame/dispatch [:server-name-changed %])]
              [re-com/input-text
               :placeholder "server url"
               :model ""
               :on-change #(re-frame/dispatch [:server-url-changed %])]
              [re-com/button
               :label "add"
               :on-click #(re-frame/dispatch [:add-server])]
              [re-com/button
               :label "@Consul"
               :on-click #(re-frame/dispatch [:read-consul])]
              ]]
  )

(defn servers []
  (let [servers (re-frame/subscribe [:get-servers])]
    [re-com/h-box
     :children [(for [[server-name server-url] @servers]
                  [server-box server-name server-url])]]))

(defn main-panel []
  (fn []
    [re-com/v-box
     :height "100%"
     :children [[title]
                [add-bar]
                [servers]]]))
