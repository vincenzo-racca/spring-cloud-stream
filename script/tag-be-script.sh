#!/bin/bash

# primo argomento: tag da creare, secondo argomento (opzionale) --create-next-release
# esempio ./tag-be-script 0.0.1-RC-1 --create-next-release #se si vuole creare il branch next-release
# esempio /tag-be-script 0.0.1-RC1 #se non si vuole creare il branch next-release
n_args=$#
if [ $n_args -lt 1 ]; then
echo "No argument is given"
exit 1
fi
tag=$1

if [[ $2 == *"create-next-release"* ]]; then
  create_next_release_branch="y"
else
  create_next_release_branch="n"
fi

echo "Creating tag: $tag creating next release?: $create_next_release_branch"


# Esegui git fetch
git fetch

# Sposta sul ramo develop e allineati al ramo remoto
git checkout develop
git pull

# Verifica se il tag contiene "RC"
if [[ $tag == *"RC"* ]]; then # Ramo RC
  # Prendi versione corrente del progetto
  current_version=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
  suffix="-SNAPSHOT"
  release_branch_name=${current_version%"$suffix"} #esempio, da 0.0.1-SNAPSHOT a 0.0.1
  if git rev-parse --quiet --verify release/"$release_branch_name"; then
    git checkout release/"$release_branch_name"
    git pull
  else
    git checkout -b release/"$release_branch_name"
  fi
  git merge --no-edit develop
  # Modifica la versione del pom.xml con il tag dato in input (esempio da 0.0.1-SNAPSHOT a 0.0.1-RC-1)
  ./mvnw build-helper:parse-version versions:set -DnewVersion=$tag -U versions:commit
  git add pom.xml
  git commit -m "[$tag] Modifica versione nel pom.xml"
  git push origin release/"$release_branch_name"
  # Crea il tag dal ramo develop con il prefisso "v" (esempio v0.0.1-RC-1)
  git tag -a "v$tag" -m "Release Candidate $tag"
  git push origin "v$tag"

  if [[ $create_next_release_branch == "y" ]]; then
    git checkout develop
    # Verifica se esiste il branch "next-release"
    if git rev-parse --quiet --verify next-release; then
      git checkout next-release
    else
      git checkout -b next-release
    fi
    git pull origin next-release
    # Incrementa la versione patch nel pom.xml e pusha sul ramo next-release (esempio da 0.0.1-RC-1 a 0.0.2-SNAPSHOT)
    ./mvnw build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}-SNAPSHOT -U versions:commit
    next_version=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
    git add pom.xml
    git commit -m "[$next_version] Incrementa versione minor nel pom.xml"
    git push origin next-release
  fi
  git checkout develop

#  # Ripristino la versione originale nel branch develop (esempio da 0.0.1-RC-1 a 0.0.1-SNAPSHOT)
#  git checkout develop
#  ./mvnw build-helper:parse-version versions:set -DnewVersion=$current_version -U versions:commit
#  git add pom.xml
#  git commit -m "Ripristino versione develop nel pom.xml $current_version"
#  git push origin develop


else # Ramo RELEASE
#  # Ottieni l'ultimo tag RC creato
#  last_rc_tag=$(git tag -l 'v*-RC*' --sort=-v:refname | head -n 1)
#
#  # Verifica se sono stati creati tag RC in precedenza
#  if [[ -z $last_rc_tag ]]; then
#    echo "Nessun tag RC trovato."
#    exit 1
#  fi
# Crea il branch release/<tag_release> dall'ultimo tag RC creato, per modificare la versione del pom (esempio da 0.0.1-RC-2 a 0.0.1)
#  git checkout tags/$last_rc_tag -b release/$tag
  if git rev-parse --quiet --verify release/$tag; then
    git checkout release/$tag
  else
    echo "Branch release/$tag does not exit. Creating it from develop branch..."
    git checkout -b release/$tag
  fi
  git pull origin release/$tag
  ./mvnw build-helper:parse-version versions:set -DnewVersion=$tag -U versions:commit
  git add pom.xml
  git commit -m "[$tag] Create Release Version"
  git push origin release/$tag
#  gh pr create --fill --base main
  git checkout main
  git pull origin main
  git merge release/$tag
  git push origin main
  # Crea il tag sul branch main (esempio v0.0.1)
#  git tag -a "v$tag" -m "Release $tag"
  echo "Creating tag v$tag"
  gh release create v$tag --generate-notes

  # Mergiare develop prima con main e poi con next-release
#  git checkout develop
#  git merge main
#  git merge next-release
#  git push origin develop
fi