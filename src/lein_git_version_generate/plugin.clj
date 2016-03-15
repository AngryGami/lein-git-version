(ns lein-git-version-generate.plugin
  (:require [robert.hooke])
  (:use
   clojure.pprint
   [leiningen.git-version :only [git-version]]))

(defn wrap-with-hook [f & args]
  (do
    (try
      (apply git-version args)
      (catch Exception e (println "lein-git-version plugin: exception " (.getMessage e))))
    (apply f args)))

(defn hooks []
  (robert.hooke/add-hook #'leiningen.compile/compile #'wrap-with-hook))
