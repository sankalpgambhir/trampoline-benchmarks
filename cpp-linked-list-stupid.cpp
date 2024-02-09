#include <iostream>
#include <string>
#include <chrono>
#include <vector>
#include <numeric>

class list {
    public:
        list * next;
        int value;

        list(int value, list* next) {
            this->value = value;
            this->next = next;
        }

        long length(int acc = 1) {
            if (this->next == nullptr) {
                return acc;
            }
            else {
                return this->next->length(1 + acc);
            }
        }

        std::string to_string() {
            if (this->next == nullptr) {
                return std::to_string(this->value);
            }
            else {
                return std::to_string(this->value) + " " + this->next->to_string();
            }
        }
};

list* duplicate(list* l) {
    if (l->next == nullptr) {
        return new list(l->value, l);
    }
    else {
        list* m1 = new list(l->value, nullptr);
        list* m2 = new list(l->value, m1);
        m1->next = duplicate(l->next);
        return m2;
    }
}

// tail recursive version of the function above
list* duplicate_tail(list* l, list* head, list* tail) {
    if (l->next == nullptr) {
        auto m1 = new list(l->value, nullptr);
        auto m2 = new list(l->value, m1);
        tail->next = m2;
        return head;
    }
    else {
        auto m1 = new list(l->value, nullptr);
        auto m2 = new list(l->value, m1);
        tail->next = m2;
        return duplicate_tail(l->next, head, m1);
    }
}

long run() {
    list* l = new list(-1, nullptr);
    list* l1 = l;
    for (int i = 0; i < 10'000'000; i++) {
        l = new list(i, l);
    }
    auto head = new list(0, nullptr);
    auto start = std::chrono::high_resolution_clock::now();
    list* m = duplicate_tail(l, head, head)->next;
    auto end = std::chrono::high_resolution_clock::now();

    delete head;
    delete l;

    return (end.time_since_epoch() - start.time_since_epoch()).count() / 1e6;
}

int main() {
    const unsigned int bench_num = 25;
    auto times = std::vector<long>(bench_num);

    for(int i = 0; i < bench_num; i++) {
        times.emplace_back(run());
    }

    auto mean = std::accumulate(times.begin(), times.end(), 0) / bench_num;

    std::cout << "Average time: " << mean << " ms" << std::endl;
}
