example stanford nlp scala
===

Some NLP with Zio and Stanford CoreNLP


| Name         | Description                   | Link                          |
|--------------|-------------------------------|-------------------------------|
| Stanford NLP | NLP core library                              | https://stanfordnlp.github.io |
| CLU Lab NLP  | NLP support library; University of Arizona    | https://github.com/clulab     |


### caseless models

The system defaults to the caseless models which are found in the large corenlp models jar `stanford-english-corenlp-2018-10-05-models.jar`

This jar can be placed under `lib` in the installation dir, which defaults to `/usr/local/zz`


### test data

See the [data](data/) directory for subset of samples.  A GitLFS repo will be linked later.


### reference
- https://github.com/stanfordnlp/CoreNLP
- https://stanfordnlp.github.io/CoreNLP/annotators.html
- https://web.stanford.edu/class/cs124/lec/postagging.pdf
- https://nlp.stanford.edu/software/dependencies_manual.pdf
- https://stanfordnlp.github.io/CoreNLP/ner.html#customizing-the-fine-grained-ner
- https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/ie/NERFeatureFactory.html
