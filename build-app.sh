#!/bin/bash

cd /mnt/d/PROJECT/Agentic-DevOps-with-Claude-Code/

echo "════════════════════════════════════════════════════"
echo "📦 Building Spring Boot Application"
echo "════════════════════════════════════════════════════"

# Step 1: Clean and build
echo "🧹 Cleaning and building..."
mvn clean package -DskipTests

# Step 2: Check if build succeeded
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    # Step 3: Find the JAR
    JAR_FILE=$(ls target/*.jar 2>/dev/null | head -1)
    
    if [ -n "$JAR_FILE" ]; then
        echo "📦 JAR file created: $JAR_FILE"
        echo "📊 JAR size: $(du -h $JAR_FILE | cut -f1)"
        
        echo ""
        echo "🚀 To run the application:"
        echo "   java -jar $JAR_FILE"
        echo ""
        echo "🌐 Application will be available at:"
        echo "   http://localhost:8080"
    else
        echo "❌ JAR file not found"
    fi
else
    echo "❌ Build failed"
    echo "Check the error messages above"
fi

echo "════════════════════════════════════════════════════"
