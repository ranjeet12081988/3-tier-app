#!/bin/bash

echo "════════════════════════════════════════════════════"
echo "🚀 Triggering GitHub Actions"
echo "════════════════════════════════════════════════════"

cd /mnt/d/PROJECT/Agentic-DevOps-with-Claude-Code/

# Check if there are changes
if git status --porcelain | grep -q .; then
    echo "📝 Changes detected, pushing..."
    git add .
    
    # Ask for commit message
    echo ""
    read -p "Enter commit message (default: 'Trigger GitHub Actions'): " commit_msg
    commit_msg=${commit_msg:-"Trigger GitHub Actions"}
    
    git commit -m "$commit_msg"
    
    # Push to current branch
    CURRENT_BRANCH=$(git branch --show-current)
    echo "⬆️  Pushing to $CURRENT_BRANCH..."
    git push origin $CURRENT_BRANCH
    
    echo ""
    echo "✅ Changes pushed successfully!"
else
    echo "✅ No changes to push"
    
    # Ask if user wants to force trigger without changes
    echo ""
    read -p "Force trigger workflow without changes? (y/N): " force_trigger
    if [[ $force_trigger == "y" || $force_trigger == "Y" ]]; then
        echo "📝 Creating empty commit to trigger workflow..."
        git commit --allow-empty -m "Trigger GitHub Actions manually"
        git push origin $(git branch --show-current)
        echo "✅ Workflow triggered!"
    fi
fi

echo ""
echo "════════════════════════════════════════════════════"
echo "📊 GitHub Actions Status:"
echo "   🔗 https://github.com/ranjeet12081988/3-tier-app/actions"
echo ""
echo "📋 Quick Commands:"
echo "   gh actions list -R ranjeet12081988/3-tier-app"
echo "   gh actions view -R ranjeet12081988/3-tier-app"
echo "════════════════════════════════════════════════════"
