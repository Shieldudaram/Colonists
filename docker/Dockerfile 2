FROM gradle:9.2.0-jdk25
WORKDIR /workspace
ENV GRADLE_USER_HOME=/home/gradle/.gradle
ENV TZ=America/Los_Angeles
# Python tooling (Debian/Ubuntu base)
RUN apt-get update && apt-get install -y \
    python3 python3-pip python3-venv ca-certificates curl \
  && rm -rf /var/lib/apt/lists/*

RUN apt-get update && apt-get install -y --no-install-recommends ripgrep \
  && rm -rf /var/lib/apt/lists/*


# Create an isolated Python env so pip can install packages (PEP 668 safe)
ENV VIRTUAL_ENV=/opt/venv
RUN python3 -m venv $VIRTUAL_ENV
ENV PATH="$VIRTUAL_ENV/bin:$PATH"

# Install OpenAI SDK into the venv
RUN pip install --no-cache-dir --upgrade pip openai

RUN apt-get update && apt-get install -y --no-install-recommends ripgrep \
  && rm -rf /var/lib/apt/lists/*