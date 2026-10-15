#!/bin/bash

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}🚀 GitHub Repository Deployment with Terraform${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════${NC}"

# ==========================================
# Load environment variables
# ==========================================
if [ -f .env ]; then
    source .env
fi

# ==========================================
# Check GitHub Token
# ==========================================
if [ -z "$GITHUB_TOKEN" ] || [ "$GITHUB_TOKEN" = "ghp_xxxxxxxxxxxxxxxxxxxx" ]; then
    echo -e "${YELLOW}⚠️  Enter GitHub Personal Access Token:${NC}"
    echo -e "${YELLOW}   (Generate at: https://github.com/settings/tokens)${NC}"
    read -s GITHUB_TOKEN
    export GITHUB_TOKEN
    echo
fi

# ==========================================
# Check GitHub Owner
# ==========================================
if [ -z "$GITHUB_OWNER" ] || [ "$GITHUB_OWNER" = "your-username" ]; then
    echo -e "${YELLOW}⚠️  Enter GitHub Username or Organization:${NC}"
    read GITHUB_OWNER
    export GITHUB_OWNER
fi

# ==========================================
# Set Repository Name
# ==========================================
if [ -z "$REPO_NAME" ] || [ "$REPO_NAME" = "3-tier-app" ]; then
    echo -e "${YELLOW}⚠️  Enter Repository Name (default: 3-tier-app):${NC}"
    read -r REPO_NAME_INPUT
    REPO_NAME=${REPO_NAME_INPUT:-3-tier-app}
    export REPO_NAME
fi

# ==========================================
# Set Branch Name
# ==========================================
if [ -z "$NEW_BRANCH" ] || [ "$NEW_BRANCH" = "develop" ]; then
    echo -e "${YELLOW}⚠️  Enter New Branch Name (default: develop):${NC}"
    read -r NEW_BRANCH_INPUT
    NEW_BRANCH=${NEW_BRANCH_INPUT:-develop}
    export NEW_BRANCH
fi

echo -e "${GREEN}✅ Configuration:${NC}"
echo -e "   👤 Owner: ${BLUE}${GITHUB_OWNER}${NC}"
echo -e "   📁 Repository: ${BLUE}${REPO_NAME}${NC}"
echo -e "   🌿 Default Branch: ${BLUE}main${NC}"
echo -e "   🌿 New Branch: ${BLUE}${NEW_BRANCH}${NC}"

# ==========================================
# Create Terraform variables
# ==========================================
echo -e "${YELLOW}📝 Creating Terraform variables...${NC}"

# Properly quoted terraform.tfvars
cat > terraform/terraform.tfvars << EOF2
github_token     = "${GITHUB_TOKEN}"
github_owner     = "${GITHUB_OWNER}"
repo_name        = "${REPO_NAME}"
repo_description = "3-Tier Spring Boot Application"
repo_visibility  = "private"
default_branch   = "main"
new_branch       = "${NEW_BRANCH}"
branch_protection_enabled = true
EOF2

echo -e "${GREEN}✅ terraform.tfvars created${NC}"

# ==========================================
# Deploy with Terraform
# ==========================================
cd terraform || exit

echo -e "${YELLOW}🔧 Initializing Terraform...${NC}"
terraform init

echo -e "${YELLOW}✅ Validating Terraform configuration...${NC}"
terraform validate

echo -e "${YELLOW}📋 Planning infrastructure changes...${NC}"
terraform plan

# Ask for confirmation
echo -e "${YELLOW}❓ Apply these changes to GitHub? (y/n)${NC}"
read -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${RED}❌ Deployment cancelled${NC}"
    exit 1
fi

echo -e "${YELLOW}🚀 Deploying infrastructure to GitHub...${NC}"
terraform apply -auto-approve

# ==========================================
# Get outputs
# ==========================================
REPO_URL=$(terraform output -raw repository_url 2>/dev/null)
REPO_SSH=$(terraform output -raw repository_ssh_url 2>/dev/null)
CLONE_CMD=$(terraform output -raw clone_command 2>/dev/null)

cd ..

# ==========================================
# Push code to GitHub
# ==========================================
echo -e "${YELLOW}📦 Preparing to push code to GitHub...${NC}"

# Initialize git if not exists
if [ ! -d ".git" ]; then
    echo -e "${YELLOW}📦 Initializing Git repository...${NC}"
    git init
fi

# Set the remote URL properly
git remote remove origin 2>/dev/null
git remote add origin "https://${GITHUB_TOKEN}@github.com/${GITHUB_OWNER}/${REPO_NAME}.git"

# Add all files
git add .
git commit -m "Initial commit - 3-Tier Spring Boot App" || echo "No changes to commit"

# Push to main
echo -e "${YELLOW}⬆️  Pushing to main branch...${NC}"
git branch -M main
git push -u origin main --force

# Create and push new branch
echo -e "${YELLOW}⬆️  Creating and pushing to ${NEW_BRANCH} branch...${NC}"
git checkout -b "$NEW_BRANCH" 2>/dev/null || git checkout "$NEW_BRANCH"
git push -u origin "$NEW_BRANCH"

# Switch back to main
git checkout main

# ==========================================
# Display results
# ==========================================
echo -e "${BLUE}════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ Deployment Complete!${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════${NC}"
echo -e ""
echo -e "${GREEN}📦 Repository Created:${NC}"
echo -e "   🔗 URL: ${BLUE}${REPO_URL}${NC}"
echo -e "   🔗 SSH: ${BLUE}${REPO_SSH}${NC}"
echo -e "   📁 Name: ${BLUE}${REPO_NAME}${NC}"
echo -e "   👁️  Visibility: ${BLUE}Private${NC}"
echo -e ""
echo -e "${GREEN}🌿 Branches:${NC}"
echo -e "   🌿 Main: ${BLUE}main${NC}"
echo -e "   🌿 New: ${BLUE}${NEW_BRANCH}${NC}"
echo -e ""
echo -e "${GREEN}🔧 Clone Commands:${NC}"
echo -e "   HTTPS: ${BLUE}${CLONE_CMD}${NC}"
echo -e "   SSH:   ${BLUE}git clone ${REPO_SSH}${NC}"
echo -e ""
echo -e "${GREEN}📊 View Repository:${NC}"
echo -e "   ${BLUE}${REPO_URL}${NC}"
echo -e ""
echo -e "${BLUE}════════════════════════════════════════════════════${NC}"
