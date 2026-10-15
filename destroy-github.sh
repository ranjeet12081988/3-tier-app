#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${RED}⚠️  WARNING: This will delete the GitHub repository!${NC}"
echo -e "${YELLOW}Are you sure? (yes/no)${NC}"
read -r CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    echo -e "${GREEN}❌ Destruction cancelled${NC}"
    exit 0
fi

cd terraform || exit
terraform destroy -auto-approve
cd ..

echo -e "${GREEN}✅ Repository destroyed${NC}"
