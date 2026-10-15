#!/bin/bash

echo "════════════════════════════════════════════════════"
echo "🔧 GitHub Actions Manager"
echo "════════════════════════════════════════════════════"

cd /mnt/d/PROJECT/Agentic-DevOps-with-Claude-Code/

echo ""
echo "Choose an option:"
echo "1. Trigger workflow (with changes)"
echo "2. Trigger workflow (without changes - force)"
echo "3. Check status"
echo "4. Trigger on specific branch"
echo "5. View latest run logs"
echo "6. Rerun failed workflow"
echo "7. Cancel running workflow"
echo "8. Exit"
echo ""
read -p "Enter choice (1-8): " choice

case $choice in
    1)
        ./trigger-actions.sh
        ;;
    2)
        git commit --allow-empty -m "Force trigger GitHub Actions"
        git push origin $(git branch --show-current)
        echo "✅ Workflow triggered!"
        ;;
    3)
        ./check-actions.sh
        ;;
    4)
        ./trigger-branch.sh
        ;;
    5)
        if command -v gh &> /dev/null; then
            gh actions view -R ranjeet12081988/3-tier-app --log
        else
            echo "GitHub CLI not installed"
        fi
        ;;
    6)
        if command -v gh &> /dev/null; then
            RUN_ID=$(gh actions list -R ranjeet12081988/3-tier-app --limit 1 --json runId --jq '.[0].runId')
            gh actions rerun $RUN_ID -R ranjeet12081988/3-tier-app
            echo "✅ Re-run triggered for run: $RUN_ID"
        else
            echo "GitHub CLI not installed"
        fi
        ;;
    7)
        if command -v gh &> /dev/null; then
            RUN_ID=$(gh actions list -R ranjeet12081988/3-tier-app --limit 1 --json runId --jq '.[0].runId')
            gh actions cancel $RUN_ID -R ranjeet12081988/3-tier-app
            echo "✅ Cancel triggered for run: $RUN_ID"
        else
            echo "GitHub CLI not installed"
        fi
        ;;
    8)
        echo "👋 Exiting..."
        exit 0
        ;;
    *)
        echo "❌ Invalid choice"
        ;;
esac

echo ""
echo "════════════════════════════════════════════════════"
