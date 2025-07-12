big_model=mistral-small3.2:latest
smaller_model=gemma3n:e4b

ollama pull $smaller_model
#ollama serve
ollama run $smaller_model


