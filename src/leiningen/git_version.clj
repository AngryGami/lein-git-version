(ns leiningen.git-version
  (:require [leiningen.help]
            [leiningen.jar]
            [leiningen.compile]
            [leiningen.core.main]
            [leiningen.core.project]
            [robert.hooke]
            [leiningen.test]
            [clojure.java.io])
  (:use
   [clojure.java.shell :only [sh]]))

(defn get-git-version
  []
  (apply str (rest (clojure.string/trim
                    (:out (sh
                           "git" "describe" "--match" "v*.*"
                           "--abbrev=4" "--dirty=**DIRTY**"))))))

(defn get-git-ref
  []
  (apply str (clojure.string/trim
                    (:out (sh
                           "git" "rev-parse" "--verify" "HEAD")))))

(defn get-git-status
  []
  (apply str (clojure.string/trim
                    (:out (sh
                           "git" "status" "--porcelain")))))

;(defn git-version
;  "Show project version, as tagged in git."
;  ^{:doc "Show git project version"}
;  [project & args]
;  (println (get-git-version)))

;(def mget-git-version (memoize get-git-version))
;(def mget-git-ref (memoize get-git-version))
;(def mget-git-status (memoize get-git-status))

(defn git-version
  "Generate version namespace."
  ^{:doc "Generate version namespace"}
  [project & args]
  (println "Generating version namespace...")
  (let [git-ver (get-git-version)
        git-ver-str (if (empty? git-ver) (:version project) git-ver)
        _ (println "version:" git-ver-str)
        git-ref (get-git-ref)
        _ (println "git ref:" git-ref)
        git-status (get-git-status)
        _ (println "git status:" (pr-str git-status))
        code (str
              ";; Do not edit.  Generated by lein-git-version plugin.\n"
              "(ns " (:name project) ".version)\n"
              "(def version \"" git-ver-str "\")\n"
              "(def gitref \"" git-ref "\")\n"
              "(def gitstatus \"" git-status "\")\n")
        proj-dir (.toLowerCase (.replace (:name project) \- \_))
        _ (println "proj-dir:" proj-dir)
        filename (if (:git-version-path project)
                   (str (:git-version-path project) "/version.clj")
                   (str (or (first (:generated-paths project)) "generated/src") "/"
                        proj-dir "/version.clj"))
        _ (println "filename:" filename)
        f (clojure.java.io/as-file filename)]
    (when-not (.exists f) (clojure.java.io/make-parents filename))
    (spit f code)))
