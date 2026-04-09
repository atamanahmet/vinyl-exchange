.PHONY: start stop

include .env
export

start:
	docker compose up --build -d
	@echo "Waiting for backend to be ready..."
	@until docker inspect -f '{{.State.Health.Status}}' vx-backend 2>/dev/null | grep -q "healthy"; do sleep 2; done
	@echo "Waiting for frontend to be ready..."
	@until curl -s -o /dev/null -w "%{http_code}" http://localhost:80 | grep -q "200"; do sleep 2; done
	@echo ""
	@echo "==============================="
	@echo "  Vinyl Exchange is running"
	@echo "==============================="
	@echo "  Frontend : http://localhost:80"
	@echo "  Backend  : http://localhost:$(SERVER_PORT)"
	@echo "==============================="

stop:
	docker compose down